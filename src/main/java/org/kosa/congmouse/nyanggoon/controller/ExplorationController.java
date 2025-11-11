package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Security;
import java.util.List;

@Tag(name="문화재 탐방기", description="문화재 탐방기 CRUD 관련 API")
@RestController
@RequestMapping("/explorations")
@RequiredArgsConstructor
@Slf4j
public class ExplorationController {
    private final ExplorationService explorationService;

    @Operation(summary="문화재 탐방기 리스트 조회", description="문화재 탐방기 리스트를 무한스크롤로 조회한다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExplorationDetailDto.class)))
    )
    @GetMapping(params={"page", "size"})
    public ResponseEntity<ApiResponseDto<Page<ExplorationDetailDto>>> getExplorationListInfiniteScroll(@Parameter(description = "페이지 숫자", example="1") @RequestParam Long page, @Parameter(description = "조회할 문화재 탐방기 숫자", example="2")@RequestParam Long size){
        Page<ExplorationDetailDto> explorationDetailDtoPage = explorationService.getExplorationInfiniteScrollList(page, size);
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDtoPage, "문화재 탐방기 무한스크롤 조회 성공"));
    }

    // 검색
    @Operation(summary="문화재 탐방기 검색", description="키워드가 포함된 문화재 탐방기 글을 검색한다.")
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = ExplorationDetailDto.class)))
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> getSearchExplorationPost(@Parameter(description = "검색 키워드", example="광화문") @RequestParam String keyword){
        List<ExplorationDetailDto> explorationDetailDtoList = explorationService.searchExploration(keyword);
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDtoList, "문화재 탐방기 검색 조회 성공"));
    }

    @Operation(summary="문화재 탐방기 작성", description = "문화재 탐방기 글을 작성한다.")

    @PostMapping("")
    public ResponseEntity<ApiResponseDto<?>> postExploration(@RequestPart("dto") ExplorationCreateDto explorationCreateDto, @RequestPart(name = "images", required = false) List<MultipartFile> imageFileList) throws IOException {
        ExplorationDetailDto explorationDetailDto = explorationService.createExploration(explorationCreateDto, imageFileList);
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDto, "문화재 탐방기 작성 완료"));
    }

    @Operation(summary="문화재 탐방기 조회", description="문화재 탐방기 개별 글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ExplorationDetailDto> getExploration(@Parameter(description = "조회할 문화재 탐방기 id", example="1") @PathVariable Long id){
        ExplorationDetailDto explorationDetailDto = explorationService.viewExploration(id);
        return ResponseEntity.status(HttpStatus.OK).body(explorationDetailDto);
    }

    @Operation(summary="문화재 탐방기 수정", description = "이미 작성한 문화재 탐방기를 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity patchExploration(@Parameter(description = "수정할 문화재 탐방기 id", example="") @PathVariable Long id, @RequestPart("dto") ExplorationUpdateDto explorationUpdateDto, @RequestPart(name = "images", required = false) List<MultipartFile> imageFilelist, @AuthenticationPrincipal CustomMemberDetails memberDetails) throws IOException {
        log.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.debug("글쓴이id={} 수정하는사람id={}", explorationUpdateDto.getMemberId(), memberDetails.getMember());
        log.debug("사진들{}", imageFilelist);
        ExplorationDetailDto explorationDetailDto = explorationService.editExploration(explorationUpdateDto,imageFilelist, memberDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(explorationDetailDto);
    }

    @Operation(summary="문화재 탐방기 삭제", description="문화재 탐방기를 삭제합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    )
    @DeleteMapping("/{id}")
    public ResponseEntity deleteExploration(@Parameter(description = "삭제할 문화재 탐방기 id", example="") @PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        explorationService.deleteExploration(id, memberDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 북마크 생성 요청
    @Operation(summary="문화재 탐방기 북마크 생성", description="문화재 탐방기 북마크를 생성합니다.")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    )
    @PostMapping("/bookmarks")
    public ResponseEntity<ApiResponseDto<?>> postExplorationBookmark(@RequestBody ExplorationBookmarkRequestDto explorationBookmarkRequestDto){
        log.debug("{} {}", explorationBookmarkRequestDto.getExplorationId(), explorationBookmarkRequestDto.getMemberId());
        explorationService.createExplorationBookmark(explorationBookmarkRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(null, "북마크 생성 완료"));
    }

    @Operation(summary="문화재 탐방기 북마크 삭제", description="문화재 탐방기 북마크를 삭제합니다")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ApiResponseDto.class)))
    )
    @DeleteMapping("/bookmarks")
    public ResponseEntity<ApiResponseDto<?>> deleteExplorationBookmark(@Parameter(description="문화재 탐방기 북마크 DTO")@RequestBody ExplorationBookmarkRequestDto explorationBookmarkRequestDto) {
        log.debug("{} {}", explorationBookmarkRequestDto.getExplorationId(), explorationBookmarkRequestDto.getMemberId());
        explorationService.deleteExplorationBookmark(explorationBookmarkRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(null, "북마크 삭제 완료"));
    }

    // 북마크 체크 여부 조회
    @Operation(summary="문화재 탐방기 북마크 조회", description="문화재 탐방기 북마크를 조회합니다")
    @GetMapping("/bookmarks")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ApiResponseDto.class), examples = @ExampleObject(
                    value = """
                    {
                      "success" : "success", 
                      "message" : "문화재 탐방기 신고를 완료하였습니다.",
                      "data" : "true",
                      "code" : "200",
                      "timestamp" : "2025-11-10T05:57:26.672Z"
                    }
                    """
            )))
    )
    public ResponseEntity<ApiResponseDto<Boolean>> getExplorationBookmarkChecked(@Parameter(description = "멤버 아이디", example="1")@RequestParam Long memberId, @Parameter(description="탐방기 아이디", example="1")Long explorationId) {
        log.debug("{} {}", memberId, explorationId);
        Boolean result = explorationService.checkExplorationBookmarked(memberId, explorationId);
        return ResponseEntity.ok(ApiResponseDto.success(result, "북마크 여부 조회 완료"));
    }

    /**
     * 문화재 탐방기 게시글 신고 요청
     * @param reportCreateRequestDto
     * @return 신고 요청에 대한 결과를 반환
     */
    @Operation(summary="문화재 탐방기 신고", description="문화재 탐방기 신고를 등록합니다")
    @PostMapping("/reports")
    @ApiResponses(
            @ApiResponse(responseCode = "200", description = "", content = @Content(schema = @Schema(implementation = ReportResponseDto.class)))
    )
    public ResponseEntity<ApiResponseDto<ReportResponseDto>> postExplorationReport(@Parameter(description = "문화재 탐방기 신고 요청 DTO", example="")@RequestBody ReportCreateRequestDto reportCreateRequestDto){
        ReportResponseDto explorationReportResponseDto = explorationService.createExplorationReport(reportCreateRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportResponseDto, "신고 완료"));
    }
}
