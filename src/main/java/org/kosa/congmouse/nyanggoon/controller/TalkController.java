package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.kosa.congmouse.nyanggoon.service.TalkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Slf4j
//임시 설정 입니다. (추후 삭제)
@CrossOrigin(origins = "http://localhost:5173")
//담소 컨트롤러 입니다.
public class TalkController {
    private final TalkService talkService;

    /**
     * 게시글들을 가져오는 컨트롤러 입니다.
     * @return
     */
    @GetMapping
    public ResponseEntity<?> getAllTalkList(){
        log.info("게시글들 조회 컨트롤러 작동 ok");
        List<TalkListSummaryResponseDto> talks = talkService.findAllTalkList();
        return ResponseEntity.ok(ApiResponseDto.success(talks, "게시물 목록 조회 성공"));
    }

    /**
     * 게시글을 상세 확인하는 컨트롤러 입니다.
     * 댓글도 함께 가져옵니다.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getTalkDetailById(@PathVariable Long id){
        log.info("게시글 상세 조회 컨트롤러 작동 ok");
        TalkDetailResponseDto talkDetailResponseDto = talkService.findTalkDetail(id);
        return ResponseEntity.ok(ApiResponseDto.success(talkDetailResponseDto, "담소 게시글 조회 성공"));
    }

    /**
     * 게시글을 작성하는 컨트롤러 입니다.
     * @param talkCreateRequestDto
     * @return
     */
    @PostMapping("")
    public ResponseEntity<?> createTalk(@RequestBody TalkCreateRequestDto talkCreateRequestDto){
            talkService.createTalk(talkCreateRequestDto);
        // ApiResponseDto 의 표준화된 형식으로 응답한다
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkCreateRequestDto, "게시글이 작성되었습니다."));
    }

    /**
     * 게시글을 수정하는 컨트롤러 입니다.
     * @param talkId
     * @param talkUpdateRequestDto
     * @return
     */
    @PutMapping("/{talkId}")
    public ResponseEntity<?> updateTalkById(@PathVariable Long talkId, @RequestBody TalkUpdateRequestDto talkUpdateRequestDto){
        talkService.updateTalk(talkId, talkUpdateRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkUpdateRequestDto, "게시글이 수정되었습니다."));
    }

    /**
     * 게시글을 삭제하는 컨트롤러 입니다.
     * @param talkId
     * @return
     */
    @DeleteMapping("/{talkId}")
    public ResponseEntity<Void> deleteTalkById(@PathVariable Long talkId) {
        log.info("게시글 삭제 컨트롤러 작동 ok");
        log.info("삭제할 게시글 id : {}", talkId);
        try {
            talkService.deleteTalk(talkId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 댓글을 작성하는 컨트롤러 입니다.
     * @param talkCommentCreateRequestDto
     * @return
     */
        @PostMapping("/{talkId}/comments")
        public ResponseEntity<?> createTalkComment(@RequestBody TalkCommentCreateRequestDto talkCommentCreateRequestDto){
           TalkComment savedComment =  talkService.createTalkComment(talkCommentCreateRequestDto);
            // 응답 DTO 생성
            TalkCommentResponseDto talkCommentResponseDto = TalkCommentResponseDto.builder()
                    .talkCommentId(savedComment.getId())
                    .content(savedComment.getContent())
                    .createdAt(savedComment.getCreatedAt())
                    .memberId(savedComment.getMember().getId())
                    .nickname(savedComment.getMember().getNickname())
                    .talkId(savedComment.getTalk().getId())
                    .talkParentCommentId(savedComment.getParentComment() != null ? savedComment.getParentComment().getId() : null)
                    .build();

            // ApiResponseDto 의 표준화된 형식으로 응답한다
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkCommentResponseDto, "댓글이 작성되었습니다."));
        }

    /**
     * 댓글을 수정하는 컨트롤러 입니다.
     * @param commentId
     * @param talkCommentUpdateRequestDto
     * @return
     */
        @PutMapping("/{talkId}/comments/{commentId}")
        public ResponseEntity<?> updateTalkCommentById(@PathVariable Long talkId, @PathVariable Long commentId, @RequestBody TalkCommentUpdateRequestDto talkCommentUpdateRequestDto){
            talkService.updateTalkComment(commentId, talkCommentUpdateRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkCommentUpdateRequestDto, "댓글이 수정되었습니다."));
        }

    /**
     * 댓글을 삭제하는 컨트롤러 입니다.
     * @param talkId
     * @param commentId
     * @return
     */
        @DeleteMapping("/{talkId}/comments/{commentId}")
        public ResponseEntity<Void> deleteTalkCommentById(@PathVariable Long talkId, @PathVariable Long commentId){
            log.info("댓글 삭제 컨트롤러 작동 ok");
            log.info("댓글을 삭제할 게시글 id : {}", talkId);
            log.info("삭제할 댓글 id : {}", commentId);
            try{
                talkService.deleteTalkComemnt(commentId);
                return ResponseEntity.noContent().build();
            }
            catch(Exception e){
                return  ResponseEntity.notFound().build();
            }
    }

    //담소를 신고하는 컨트롤러 입니다,
    //담소 댓글을 신고하는 컨트롤러 입니다.
    //담소를 북마크하는 컨트롤러 입니다.
}
