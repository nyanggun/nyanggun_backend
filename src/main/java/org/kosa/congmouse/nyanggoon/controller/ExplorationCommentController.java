package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.Response;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ExplorationCommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="문화재 탐방기 댓글", description = "문화재 탐방기 댓글 관련 API")
@RequestMapping("/exploration-comments")
@RequiredArgsConstructor
@Slf4j
@RestController
public class ExplorationCommentController {

    private final ExplorationCommentService explorationCommentService;

    @Operation(summary="문화재 탐방기 댓글 작성", description = "문화재 탐방기에 댓글을 작성한다.")
    @PostMapping()
    public ResponseEntity<?> postExplorationComment(@Parameter(description="문화재 탐방기 댓글 작성 DTO")@RequestBody ExplorationCommentCreateDto explorationCommentCreateDto, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        log.debug("{}", explorationCommentCreateDto);
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.createExplorationComment(explorationCommentCreateDto, memberDetails.getMemberId());
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 생성 완료"));
    }

    @Operation(summary = "문화재 탐방기 댓글 전체 리스트 조회", description = "문화재 탐방기의 전체 댓글을 조회합니다.")
    @GetMapping()
    public ResponseEntity<?> getExplorationCommentList(){
        List<ExplorationCommentResponseDto> list = explorationCommentService.getExplorationCommentList();
        return ResponseEntity.ok(ApiResponseDto.success(list, "댓글 리스트 조회 완료"));
    }

    @Operation(summary = "문화재 탐방기 댓글 조회", description = "문화재 탐방기 개별 댓글을 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<?> getExplorationComment(@Parameter(description="문화재 탐방기 댓글 id", example="1")@PathVariable Long id){
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.getExplorationComment(id);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 조회 완료"));
    }

    @Operation(summary="문화재 탐방기 댓글 수정", description = "문화재 탐방기 댓글을 수정한다.")
    @PatchMapping("/{id}")
    public ResponseEntity<?> patchExplorationComment(@Parameter(description="문화재 탐방기 댓글 id", example="1")@PathVariable Long id, @Parameter(description="문화재 탐방기 댓글 수정 dto")@RequestBody ExplorationCommentUpdateDto explorationCommentUpdateDto, @AuthenticationPrincipal CustomMemberDetails customMemberDetails){
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.updateExplorationComment(explorationCommentUpdateDto, customMemberDetails);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 수정 완료"));
    }

    @Operation(summary="문화재 탐방기 댓글 삭제", description = "문화재 탐방기 댓글을 삭제한다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExplorationComment(@Parameter(description="문화재 탐방기 댓글 id", example="1")@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails customMemberDetails){
        explorationCommentService.deleteExplorationComment(id, customMemberDetails.getMemberId());
        return ResponseEntity.ok(ApiResponseDto.success(null, "댓글 삭제 완료"));
    }

    @Operation(summary="문화재 탐방기의 댓글을 조회", description = "해당하는 문화재 탐방기 id를 가지고 있는 문화재 탐방기 댓글을 조회한다")
    @GetMapping(params = "explorationId")
    public ResponseEntity<?> getExplorationCommentOfExploration(@Parameter(description="문화재 탐방기 id", example="1")@RequestParam Long explorationId){
        List<ExplorationCommentResponseDto> explorationCommentResponseDtoList =  explorationCommentService.getExplorationCommentListOfExploration(explorationId);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDtoList, "문화재 탐방기에 대한 댓글 조회 성공"));
    }

    //탐방기 댓글을 신고하는 컨트롤러 입니다.
    @Operation(summary="문화재 탐방기 댓글 신고", description = "문화재 탐방기 댓글을 신고한다")
    @PostMapping("/reports/comments")
    public ResponseEntity<?> postTalkCommentReport(@Parameter(description="문화재 탐방기 댓글 신고 dto")@RequestBody ReportCreateRequestDto reportCreateRequestDto){
        ReportResponseDto explorationReportResponseDto = explorationCommentService.createTalkCommentReport(reportCreateRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportResponseDto, "탐방기 댓글 신고 완료"));
    }
}
