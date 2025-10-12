package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

@RequestMapping("/exploration-comments")
@RequiredArgsConstructor
@Slf4j
@RestController
public class ExplorationCommentController {

    private final ExplorationCommentService explorationCommentService;

    @PostMapping()
    public ResponseEntity<?> postExplorationComment(@RequestBody ExplorationCommentCreateDto explorationCommentCreateDto, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        log.debug("{}", explorationCommentCreateDto);
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.createExplorationComment(explorationCommentCreateDto, memberDetails.getMemberId());
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 생성 완료"));
    }

    @GetMapping()
    public ResponseEntity<?> getExplorationCommentList(){
        List<ExplorationCommentResponseDto> list = explorationCommentService.getExplorationCommentList();
        return ResponseEntity.ok(ApiResponseDto.success(list, "댓글 리스트 조회 완료"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExplorationComment(@PathVariable Long id){
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.getExplorationComment(id);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 조회 완료"));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> patchExplorationComment(@PathVariable Long id, @RequestBody ExplorationCommentUpdateDto explorationCommentUpdateDto, @AuthenticationPrincipal CustomMemberDetails customMemberDetails){
        ExplorationCommentResponseDto explorationCommentResponseDto = explorationCommentService.updateExplorationComment(explorationCommentUpdateDto, customMemberDetails);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDto, "댓글 수정 완료"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExplorationComment(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails customMemberDetails){
        explorationCommentService.deleteExplorationComment(id, customMemberDetails.getMemberId());
        return ResponseEntity.ok(ApiResponseDto.success(null, "댓글 삭제 완료"));
    }

    @GetMapping(params = "explorationId")
    public ResponseEntity<?> getExplorationCommentOfExploration(@RequestParam Long explorationId){
        List<ExplorationCommentResponseDto> explorationCommentResponseDtoList =  explorationCommentService.getExplorationCommentListOfExploration(explorationId);
        return ResponseEntity.ok(ApiResponseDto.success(explorationCommentResponseDtoList, "문화재 탐방기에 대한 댓글 조회 성공"));
    }
}
