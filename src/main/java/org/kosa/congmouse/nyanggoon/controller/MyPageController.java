package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "마이페이지", description = "마이페이지 컨트롤러 입니다.")
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 유저의 정보를 가져오는 컨트롤러 입니다.
     * 본인 정보/다른 유저 조회 용으로도 사용 가능합니다.
     */
    @GetMapping("/{id}")
    @Operation(summary = "회원의 정보를 가져오는 컨트롤러", description = "회원의 정보를 조회합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 회원의 정보를 조회했습니다."))
    public ResponseEntity<?> getUserInfo(@Parameter(description = "회원 id", example = "")@PathVariable Long id){

        MemberResponseDto memberResponseDto = myPageService.getMemberInfo(id);

        return ResponseEntity.ok(ApiResponseDto.success(memberResponseDto , "회원 정보 조회 성공"));

    }


    /**
     * 내 정보를 수정하는 컨트롤러 입니다.
     */
    @PutMapping("/{id}")
    @Operation(summary = "회원의 정보를 수정하는 컨트롤러", description = "회원의 정보를 수정합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 회원의 정보를 수정했습니다."))

    public ResponseEntity<?> updateUserInfo(@Parameter(description = "회원 id", example = "")@PathVariable Long id, @RequestBody MemberUpdateRequestDto memberUpdateRequestDto){

        TokenResponse token = myPageService.updateUserInfo(id, memberUpdateRequestDto);

        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 정보 수정 성공"));

    }

    /**
     * 회원 탈퇴하는 컨트롤러 입니다.
     */

    @DeleteMapping("/{id}")
    @Operation(summary = "회원 탈퇴하는 컨트롤러", description = "회원 탈퇴합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 회원탈퇴했습니다."))

    public ResponseEntity<?> deleteUserInfo(@Parameter(description = "회원 id", example = "")@PathVariable Long id){
        myPageService.deleteUserInfo(id);
        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 탈퇴 성공"));

    }


    //유저가 작성한 탐방기와 담소를 조회하는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소를 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/post")
    @Operation(summary = "회원이 작성한 탐방기와 담소를 조회하는 컨트롤러", description = "회원이 작성한 탐방기와 담소를 조회하는 컨트롤러 입니다. 무한 스크롤로 구현했습니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 게시글을 조회했습니다."))

    public ResponseEntity<?> getAllPosts(@Parameter(description = "회원 id", example = "")@PathVariable Long id,
            @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
            @RequestParam(required = false) Long cursor) {

        log.info("전체 게시글 조회 컨트롤러 작동 ok");
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        // 담소 + 탐방기 조회 (cursor 기준)
        PostCursorResponseDto<List<PostListSummaryResponseDto>> posts = myPageService.findAllPostsById(id, cursor, username);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "게시물 목록 조회 성공"));
    }


    //유저가 북마크한 탐방기와 담소 게시글을 가져오는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소를 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/bookmarkpost")
    @Operation(summary = "회원이 북마크한 탐방기와 담소를 조회하는 컨트롤러", description = "회원이 북마크한 탐방기와 담소를 조회하는 컨트롤러 입니다. 무한 스크롤로 구현했습니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 북마크한 게시글 정보를 조회했습니다."))

    public ResponseEntity<?> getBookmarkPosts(@Parameter(description = "회원 id", example = "")@PathVariable Long id,
                                         @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
                                         @RequestParam(required = false) Long cursor) {

        log.info("북마크한 게시글 조회 컨트롤러 작동 ok");

        // 담소 + 탐방기 조회 (cursor 기준)
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        PostCursorResponseDto<List<PostListSummaryResponseDto>> posts = myPageService.findBookmarkPostsById(id, cursor, username);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "게시물 목록 조회 성공"));
    }

    //유저가 작성한 탐방기와 담소 댓글을 가져오는 컨트롤러 입니다.
    //무한스크롤로 구현하므로 탐방기와 담소 댓글을 한꺼번에 조회한 후 하나로 출력해야 합니다.
    @GetMapping("/{id}/comment")
    @Operation(summary = "회원이 작성한 탐방기와 담소 댓글을 조회하는 컨트롤러", description = "회원이 작성한 탐방기와 담소 댓글을 조회하는 컨트롤러 입니다. 무한 스크롤로 구현했습니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 작성한 댓글들을 조회했습니다."))
    public ResponseEntity<?> getComment(@Parameter(description = "회원 id", example = "")@PathVariable Long id,
                                              @Parameter(description = "커서 (마지막으로 가져온 게시글 id)", example = "")
                                              @RequestParam(required = false) Long cursor) {

        log.info("댓글 조회 컨트롤러 작동 ok");

        // 담소 + 탐방기 조회 (cursor 기준)
        CommentCursorResponseDto<List<CommentResponseDto>> posts = myPageService.findCommentById(id, cursor);

        return ResponseEntity.ok(ApiResponseDto.success(posts, "댓글 목록 조회 성공"));
    }

    //회원이 작성한 사진함을 조회하는 컨트롤러 입니다.
    @GetMapping("/{id}/photobox")
    @Operation(summary = "회원이 작성한 사진함 게시글을 조회하는 컨트롤러", description = "회원이 작성한 사진함 게시글을 조회하는 컨트롤러 입니다. 무한 스크롤로 구현했습니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 정보를 조회했습니다."))
    public ResponseEntity<?> getPhotoBoxById(@Parameter(description = "회원 id", example = "")@PathVariable Long id, @Parameter(description = "커서 (마지막으로 가져오는 게시글의 id)", example = "")@RequestParam(required = false) Long cursor){
        log.info("회원이 작성한 사진함 게시글 조회 컨트롤러 작동 ok");
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = myPageService.getPhotoBoxListById(id, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "작성 사진함 게시물 목록 조회 성공"));

    }

    //회원이 북마크한 사진함 게시글을 가져오는 컨트롤러 입니다.
    @GetMapping("/{id}/photoboxbookmark")
    @Operation(summary = "회원이 북마크한 사진함 게시글을 조회하는 컨트롤러", description = "회원이 북마크한 사진함 게시글을 조회하는 컨트롤러 입니다. 무한 스크롤로 구현했습니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 북마크한 사진함 정보를 조회했습니다."))
    public ResponseEntity<?> getPhotoBoxBookmarkById(@Parameter(description = "회원 id", example = "")@PathVariable Long id, @Parameter(description = "커서 (마지막으로 가져오는 게시글의 id)", example = "")@RequestParam(required = false) Long cursor){

        log.info("회원이 북마크한 사진함 게시글 조회 컨트롤러 작동 ok");
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = myPageService.getPhotoBoxBookmarkListById(id, cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "북마크한 사진함 게시물 목록 조회 성공"));

    }

}