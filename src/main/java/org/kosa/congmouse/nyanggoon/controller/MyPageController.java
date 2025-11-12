package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.service.MemberService;
import org.kosa.congmouse.nyanggoon.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberService memberService;

    /**
     * 유저의 정보를 가져오는 컨트롤러 입니다.
     * 본인 정보/다른 유저 조회 용으로도 사용 가능합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id){

        MemberResponseDto memberResponseDto = myPageService.getMemberInfo(id);

        return ResponseEntity.ok(ApiResponseDto.success(memberResponseDto , "회원 정보 조회 성공"));

    }


    /**
     * 내 정보를 수정하는 컨트롤러 입니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestBody MemberUpdateRequestDto memberUpdateRequestDto){

        TokenResponse token = myPageService.updateUserInfo(id, memberUpdateRequestDto);

        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 정보 수정 성공"));

    }

    /**
     * 회원 탈퇴하는 컨트롤러 입니다.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserInfo(@PathVariable Long id){
        myPageService.deleteUserInfo(id);
        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 탈퇴 성공"));

    }


    //유저가 작성한 탐방기와 담소를 조회하는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소를 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/post")
    public ResponseEntity<?> getAllPosts(@PathVariable Long id,
            @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
            @RequestParam(required = false) Long cursor) {

        log.info("전체 게시글 조회 컨트롤러 작동 ok");

        // 담소 + 탐방기 조회 (cursor 기준)
        PostCursorResponseDto<List<PostListSummaryResponseDto>> posts = myPageService.findAllPostsById(id, cursor);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "게시물 목록 조회 성공"));
    }


    //유저가 북마크한 탐방기와 담소 게시글을 가져오는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소를 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/bookmarkpost")
    public ResponseEntity<?> getBookmarkPosts(@PathVariable Long id,
                                         @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
                                         @RequestParam(required = false) Long cursor) {

        log.info("북마크한 게시글 조회 컨트롤러 작동 ok");

        // 담소 + 탐방기 조회 (cursor 기준)
        PostCursorResponseDto<List<PostListSummaryResponseDto>> posts = myPageService.findBookmarkPostsById(id, cursor);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "게시물 목록 조회 성공"));
    }

    //유저가 작성한 탐방기와 담소 댓글을 가져오는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소 댓글을 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/comment")
    public ResponseEntity<?> getComment(@PathVariable Long id,
                                              @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
                                              @RequestParam(required = false) Long cursor) {

        log.info("댓글 조회 컨트롤러 작동 ok");

        // 담소 + 탐방기 조회 (cursor 기준)
        CommentCursorResponseDto<List<CommentResponseDto>> posts = myPageService.findCommentById(id, cursor);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "댓글 목록 조회 성공"));
    }

    //회원이 작성한 사진함을 조회하는 컨트롤러 입니다.
    @GetMapping("/{id}/photobox")
    public ResponseEntity<?> getPhotoBoxById(@PathVariable Long id, @RequestParam(required = false) Long cursor){
        log.info("회원이 작성한 사진함 게시글 조회 컨트롤러 작동 ok");
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = myPageService.getPhotoBoxListById(id, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "작성 사진함 게시물 목록 조회 성공"));

    }

    //회원이 북마크한 사진함 게시글을 가져오는 컨트롤러 입니다.
    @GetMapping("/{id}/photoboxbookmark")
    public ResponseEntity<?> getPhotoBoxBookmarkById(@PathVariable Long id, @RequestParam(required = false) Long cursor){

        log.info("회원이 북마크한 사진함 게시글 조회 컨트롤러 작동 ok");
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = myPageService.getPhotoBoxBookmarkListById(id, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "북마크한 사진함 게시물 목록 조회 성공"));

    }

}