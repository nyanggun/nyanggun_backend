package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCommentCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCommentResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
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

//    @GetMapping()
//    public ResponseEntity<?> getExplorationCommentList(){
//        List<ExplorationCommentResponseDto> explorationCommentResponseDtoList = explorationCommentService.getExplorationCommentList();
//    }
}
