package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.HeritageEncyclopediaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "문화재 도감", description = "문화재 도감 관련 API")
@RestController
@RequestMapping("/heritages")
@RequiredArgsConstructor
@Slf4j
public class HeritageEncyclopediaController {

    private final HeritageEncyclopediaService heritageEncyclopediaService;

    // 스케줄러 자동 문화재 db 저장
    @Scheduled(cron = "0 0 3 1 1 *") // 1월 1일 새벽 3시
    @Scheduled(cron = "0 0 3 1 7 *") // 7월 1일 새벽 3시
    @Operation(
            summary = "[스케줄러]데이터베이스에 문화재 리스트 저장(국가유산성 API 요청)",
            description = "문화재 도감에 보여줄 문화재 리스트를 국가유산성 API를 요청해 저장합니다.이 API는 매년 1월 1일, 7월 1일에 " +
                    "자동으로 실행됩니다.",
            security = {@SecurityRequirement(name = "JWT Token")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    public ResponseEntity<?> saveHeritageAuto(){
        heritageEncyclopediaService.saveHeritageList();
        return ResponseEntity.ok("문화재 정보 저장 완료");
    }

    // 관리자 수동 문화재 db 저장
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
        summary = "[관리자전용]데이터베이스에 문화재 리스트 저장(국가유산성 API 요청)",
        description = "문화재 도감에 보여줄 문화재 리스트를 국가유산성 API를 요청해 저장합니다.이 API는 관리자(Admin) 권한이 있는 사용자만 호출할 수 있습니다.",
        security = {@SecurityRequirement(name = "JWT Token")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @PostMapping("/save")
    public ResponseEntity<?> saveHeritageByAdmin(){
        heritageEncyclopediaService.saveHeritageList();
        return ResponseEntity.ok("문화재 정보 저장 완료");
    }

    // 문화재 도감 리스트-가나다순
    @Operation(
            summary = "문화재 도감 가나다순 호출",
            description = "문화재 도감을 4개씩 페이지네이션 처리해 가나다순으로 호출합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = HeritageEncyclopediaResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 예시",
                            summary = "성공 시 예시 응답",
                            value = """
            {
              "success": true,
              "message": "문화재 도감 목록 조회 성공",
              "data": {
                "content": [
                  { "id": 1, "name": "서울 숭례문", "address": "서울특별시 중구 세종대로 40" },
                  { "id": 2, "name": "서울 원각사지 십층석탑", "address": "서울특별시 종로구 종로 99" }
                ],
                "pageNumber": 0,
                "pageSize": 4,
                "totalElements": 80,
                "totalPages": 20
              },
              "code": "200",
              "timestamp": "2025-11-10T12:00:00"
            }
            """
                    )
            )
    )
    @GetMapping("/list/name")
    public ResponseEntity<ApiResponseDto<?>> getHeritageEncyclopediaNameList(
            @Parameter(description = "조회할 페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 데이터 갯수", example = "4")
            @RequestParam(defaultValue = "4") int size,
            @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        Page<HeritageEncyclopediaResponseDto> result = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByKoreanName(page, size, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(result, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 리스트-인기순
    @Operation(
            summary = "문화재 도감 인기순 호출",
            description = "문화재 도감을 4개씩 페이지네이션 처리해 인기순으로 호출합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = HeritageEncyclopediaResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 예시",
                            summary = "성공 시 예시 응답",
                            value = """
            {
              "success": true,
              "message": "문화재 도감 목록 조회 성공",
              "data": {
                "content": [
                  { "id": 1, "name": "서울 숭례문", "address": "서울특별시 중구 세종대로 40" },
                  { "id": 2, "name": "서울 원각사지 십층석탑", "address": "서울특별시 종로구 종로 99" }
                ],
                "pageNumber": 0,
                "pageSize": 4,
                "totalElements": 80,
                "totalPages": 20
              },
              "code": "200",
              "timestamp": "2025-11-10T12:00:00"
            }
            """
                    )
            )
    )
    @GetMapping("/list/popular")
    public ResponseEntity<ApiResponseDto<?>> getHeritageEncyclopediaPopularList(
            @Parameter(description = "조회할 페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 데이터 갯수", example = "4")
            @RequestParam(defaultValue = "4") int size,
            @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        Page<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtosPage = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByPopular(page, size, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtosPage, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 상세페이지
    @Operation(
            summary = "문화재 도감 상세페이지 호출",
            description = "문화재에 대한 상세한 내용이 담긴 상세페이지를 호출합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HeritageEncyclopediaResponseDto.class)
                    )
            )
    })
    @GetMapping("/detail/{HeritageEncyclopediaId}")
    public ResponseEntity<ApiResponseDto<HeritageEncyclopediaResponseDto>> getHeritageEncyclopediaDetail(
            @Parameter(description = "문화재 ID", example = "47")
            @PathVariable Long HeritageEncyclopediaId,
            @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        HeritageEncyclopediaResponseDto heritageEncyclopediaResponseDto = heritageEncyclopediaService.getHeritageEncyclopediaById(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDto, "문화재 조회 성공"));
    }

    // 북마크 저장
    @Operation(
            summary = "문화재 도감 북마크 저장",
            description = "북마크한 문화재를 저장합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EncyclopediaBookmarkDto.class)
                    )
            )
    })
    @PostMapping("/bookmark/{HeritageEncyclopediaId}")
    public ResponseEntity<ApiResponseDto<?>> postBookmark(
            @Parameter(description = "문화재 ID", example = "47")
            @PathVariable Long HeritageEncyclopediaId,
            @AuthenticationPrincipal CustomMemberDetails member){
        log.info("북마크 생성");
        Long memberId = (member != null) ? member.getMemberId() : null;
        EncyclopediaBookmarkDto bookmarkDto = heritageEncyclopediaService.saveBookmark(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(bookmarkDto, "북마크 등록 성공"));
    }

    // 북마크 삭제
    @Operation(
            summary = "문화재 도감 북마크 삭제",
            description = "북마크한 문화재를 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EncyclopediaBookmarkDto.class)
                    )
            )
    })
    @DeleteMapping("/bookmark/{HeritageEncyclopediaId}")
    public ResponseEntity<ApiResponseDto<?>> deleteBookmark(
            @Parameter(description = "문화재 ID", example = "47")
            @PathVariable Long HeritageEncyclopediaId,
            @AuthenticationPrincipal CustomMemberDetails member){
        log.info("북마크 삭제");
        Long memberId = (member != null) ? member.getMemberId() : null;
        EncyclopediaBookmarkDto bookmarkDto = heritageEncyclopediaService.deleteBookmark(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(bookmarkDto, "북마크 삭제 성공"));
    }

    // 검색 기능
    @Operation(
            summary = "문화재 도감 키워드 검색 결과 호출",
            description = "문화재 도감의 키워드 검색 결과를 4개씩 페이지네이션 처리해 호출합니다."
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = HeritageEncyclopediaResponseDto.class),
                    examples = @ExampleObject(
                            name = "성공 예시",
                            summary = "성공 시 예시 응답",
                            value = """
            {
              "success": true,
              "message": "문화재 검색 성공",
              "data": {
                "content": [
                  { "id": 1, "name": "서울 숭례문", "address": "서울특별시 중구 세종대로 40" },
                  { "id": 2, "name": "서울 원각사지 십층석탑", "address": "서울특별시 종로구 종로 99" }
                ],
                "pageNumber": 0,
                "pageSize": 4,
                "totalElements": 80,
                "totalPages": 20
              },
              "code": "200",
              "timestamp": "2025-11-10T12:00:00"
            }
            """
                    )
            )
    )
    @GetMapping("/search")
    public ResponseEntity<ApiResponseDto<?>> getHeritageEncyclopediaSearch(
            @Parameter(description = "검색 키워드", example = "상감")
            @RequestParam String keyword,
            @Parameter(description = "조회할 페이지 번호(0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 데이터 갯수", example = "4")
            @RequestParam(defaultValue = "4") int size,
            @AuthenticationPrincipal CustomMemberDetails member){
        log.info("검색 시작 키워드 {} ", keyword);
        Long memberId = (member != null) ? member.getMemberId() : null;
        Page<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtosPage = heritageEncyclopediaService.searchHeritageEncyclopedia(keyword, page, size, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtosPage, "문화재 검색 성공"));
    }
}
