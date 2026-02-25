package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.kosa.congmouse.nyanggoon.service.TalkService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/talks")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "문화재 담소", description = "문화재 담소 게시판 컨트롤러 입니다.")
//담소 컨트롤러 입니다.
public class TalkController {
    private final TalkService talkService;

    
    /**
     * 게시글들을 가져오는 컨트롤러 입니다.
     * @return
     */
    @GetMapping
    @Operation(summary = "게시글들을 가져오는 컨트롤러", description = "담소 게시글들을 가져옵니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글들을 조회했습니다."))
    public ResponseEntity<?> getAllTalkList(
            @Parameter(description = "커서 (마지막으로 가져오는 게시글의 id)", example = "")
            @RequestParam(required = false) Long cursor){
        log.info("게시글들 조회 컨트롤러 작동 ok");
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        TalkCursorResponseDto<List<TalkCreateResponseDto>> talks = talkService.findAllTalkList(username, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(talks, "게시물 목록 조회 성공"));
    }

    /**
     * 게시글을 상세 확인하는 컨트롤러 입니다.
     * 댓글도 함께 가져옵니다.
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @Operation(summary = "게시글을 상세 확인하는 컨트롤러" , description = "담소 게시글 상세를 확인합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글의 상세를 조회했습니다."))
    public ResponseEntity<?> getTalkDetailById( @Parameter(description = "게시글 id", example = "")
                                                    @PathVariable Long id){
        log.info("게시글 상세 조회 컨트롤러 작동 ok");
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        TalkDetailResponseDto talkDetailResponseDto = talkService.findTalkDetail(id, username);
        return ResponseEntity.ok(ApiResponseDto.success(talkDetailResponseDto, "담소 게시글 조회 성공"));
    }

    /**
     * 게시글을 작성하는 컨트롤러 입니다.
     * @param talkCreateRequestDto
     * @return
     */
    @Operation(summary = "게시글을 작성하는 컨트롤러", description = "담소 게시글을 작성합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글을 작성했습니다."))
    @PostMapping(value = "")
    public ResponseEntity<?> createTalk(  @RequestBody TalkCreateRequestDto talkCreateRequestDto ){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
            Long talkId= talkService.createTalk(talkCreateRequestDto, username);
        // ApiResponseDto 의 표준화된 형식으로 응답한다
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkId, "게시글이 작성되었습니다."));
    }

    /**
     * 게시글을 수정하는 컨트롤러 입니다.
     * @param talkId
     * @param talkUpdateRequestDto
     * @return
     */
    @Operation(summary = "게시글을 수정하는 컨트롤러" , description = "담소 게시글을 수정합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글을 수정했습니다."))
    @PutMapping(value = "/{talkId}")
    public ResponseEntity<?> updateTalkById(@Parameter(description = "게시글 id", example = "")
                                                @PathVariable Long talkId,
                                            @RequestBody TalkUpdateRequestDto talkUpdateRequestDto){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        talkService.updateTalk(talkId, talkUpdateRequestDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkId, "게시글이 수정되었습니다."));
    }

    /**
     * 게시글을 삭제하는 컨트롤러 입니다.
     * @param talkId
     * @return
     */
    @DeleteMapping("/{talkId}")
    @Operation(summary = "게시글을 삭제하는 컨트롤러", description = "담소 게시글을 삭제합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글을 삭제했습니다."))
    public ResponseEntity<Void> deleteTalkById(@Parameter(description = "게시글 id", example = "") @PathVariable Long talkId) {
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
        @Operation(summary = "댓글을 작성하는 컨트롤러", description = "담소 게시글의 댓글을 작성합니다.")
        @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 댓글을 작성했습니다."))
        public ResponseEntity<?> createTalkComment(@io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "게시글 id 및 댓글 내용",
                required = true,
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = TalkCommentCreateRequestDto.class)
                )
        )@RequestBody TalkCommentCreateRequestDto talkCommentCreateRequestDto){
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
    @Operation(summary = "댓글을 수정하는 컨트롤러", description = "담소 게시글의 댓글을 수정합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 댓글을 수정했습니다."))
    @PutMapping("/{talkId}/comments/{commentId}")
        public ResponseEntity<?> updateTalkCommentById(@Parameter(description = "게시글 id", example = "")@PathVariable Long talkId, @Parameter(description = "댓글 id", example = "")@PathVariable Long commentId, @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "게시글 id 및 댓글 내용",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = TalkCommentUpdateRequestDto.class)
            )
    )@RequestBody TalkCommentUpdateRequestDto talkCommentUpdateRequestDto){
            talkService.updateTalkComment(commentId, talkCommentUpdateRequestDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(talkCommentUpdateRequestDto, "댓글이 수정되었습니다."));
        }

    /**
     * 댓글을 삭제하는 컨트롤러 입니다.
     * @param talkId
     * @param commentId
     * @return
     */
    @Operation(summary = "댓글을 삭제하는 컨트롤러" , description = "담소 게시글의 댓글을 삭제합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 댓글을 삭제했습니다."))
    @DeleteMapping("/{talkId}/comments/{commentId}")
        public ResponseEntity<Void> deleteTalkCommentById(@Parameter(description = "게시글 id", example = "")@PathVariable Long talkId, @Parameter(description = "댓글 id", example = "")@PathVariable Long commentId){
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

    //담소를 북마크하는 컨트롤러 입니다.
    @PostMapping("/bookmark/{talkId}")
    @Operation(summary = "담소를 북마크하는 컨트롤러", description = "담소 게시글을 북마크합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소를 북마크했습니다."))
    public ResponseEntity<?> createTalkBookmark(@Parameter(description = "게시글 id", example = "") @PathVariable Long talkId){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        talkService.createTalkBookmark(talkId, username);
        // 응답 DTO 생성

        // ApiResponseDto 의 표준화된 형식으로 응답한다
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(null, "북마크 등록 완료"));
    }

    //담소 게시글을 북마크 취소하는 컨트롤러 입니다.
    //인증 필요
    @Operation(summary = "담소 게시글을 북마크 취소하는 컨트롤러", description = "담소 게시글 북마크를 취소합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 해당 북마크를 취소했습니다."))
    @DeleteMapping("/bookmark/{talkId}")
    public ResponseEntity<Void> deletePhotoBoxBookmark(@Parameter(description = "게시글 id", example = "")@PathVariable Long talkId){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        log.info("북마크 삭제 컨트롤러 작동 ok");
        try{
            talkService.deleteTalkBookmark(talkId, username);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e){
            return  ResponseEntity.notFound().build();
        }
    }

    //담소 게시글을 검색하는 컨트롤러 입니다.
    @Operation(summary = "담소 게시글을 검색하는 컨트롤러", description = "담소 게시글을 검색합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글을 검색했습니다."))
    @GetMapping("/search")
    public ResponseEntity<?> findTalkListWithKeyword(@Parameter(description = "키워드", example = "")@RequestParam String keyword, @Parameter(description = "커서 (마지막으로 조회된 게시글 id)", example = "")Long cursor){
        log.info("게시글들 검색 컨트롤러 작동 ok");
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        TalkCursorResponseDto<List<TalkCreateResponseDto>> talks = talkService.findTalkListWithKeyword(username, keyword, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(talks, "게시물 검색 조회 성공"));
    }

   //담소를 신고하는 컨트롤러 입니다.
   @Operation(summary = "담소를 신고하는 컨트롤러", description = "담소 게시글을 신고합니다.")
   @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 게시글을 신고했습니다."))
   @PostMapping("/reports")
    public ResponseEntity<?> postTalkReport(@io.swagger.v3.oas.annotations.parameters.RequestBody(
           description = "담소 신고 내용 및 게시글 id, 회원 id",
           required = true,
           content = @Content(
                   mediaType = "application/json",
                   schema = @Schema(implementation = ReportCreateRequestDto.class)
           )
   )@RequestBody ReportCreateRequestDto reportCreateRequestDto){
        ReportResponseDto explorationReportResponseDto = talkService.createTalkReport(reportCreateRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportResponseDto, "담소 신고 완료"));
    }
    //담소 댓글을 신고하는 컨트롤러 입니다.
    @Operation(summary = "담소 댓글을 신고하는 컨트롤러", description = "담소 댓글을 신고합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 담소 댓글을 신고했습니다."))
    @PostMapping("/reports/comments")
    public ResponseEntity<?> postTalkCommentReport(@io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "담소 댓글 신고 내용 및 댓글 id, 회원 id",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReportCreateRequestDto.class)
            )
    )@RequestBody ReportCreateRequestDto reportCreateRequestDto){
        ReportResponseDto explorationReportResponseDto = talkService.createTalkCommentReport(reportCreateRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportResponseDto, "담소 댓글 신고 완료"));
    }
}
