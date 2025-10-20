package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MyPageController {

    private final MyPageService myPageService;

    /**
     * 마이페이지 초기 로드: 로그인된 사용자의 기본 프로필 정보만 불러오기
     */
    @GetMapping
    public MemberResponseDto getProfile(@AuthenticationPrincipal CustomMemberDetails customMemberDetails) {
        if (customMemberDetails == null || customMemberDetails.getMember() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }

        Long memberId = customMemberDetails.getMember().getId();
        log.info("내정보(프로필) 요청 by 사용자 ID={}", memberId);

        return myPageService.getProfileData(memberId);
    }

    /**
     * 회원 정보 수정
     */
    @PutMapping("/profileupdate")
    public MemberResponseDto updateProfile(
            @AuthenticationPrincipal CustomMemberDetails user,
            @RequestBody MemberUpdateRequestDto dto
    ) {
        Long memberId = user.getMemberId();
        log.info("회원정보 수정 요청 by ID={}", memberId);
        return myPageService.updateProfile(memberId, dto);
    }

    /**
     * 내가 작성한 게시글 목록 조회
     */
    @GetMapping("/posts")
    public List<?> getMyPosts(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 게시글 목록 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyPosts(memberId);
    }

    /**
     * 내가 북마크한 게시글 조회
     */
    @GetMapping("/bookmarks")
    public List<?> getMyBookmarks(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 북마크 게시글 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyBookmarks(memberId);
    }

    /**
     * 내가 작성한 댓글 목록 조회
     */
    @GetMapping("/comments")
    public List<?> getMyComments(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 댓글 목록 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyComments(memberId);
    }

    /**
     * 내가 업로드한 사진 조회
     */
    @GetMapping("/photos")
    public List<?> getMyPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 사진 목록 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyPhotos(memberId);
    }

    /**
     * 내가 북마크한 사진 조회
     */
    @GetMapping("/photos/bookmarked")
    public List<?> getMyBookmarkedPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 북마크 사진 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyBookmarkedPhotos(memberId);
    }

    /**
     * 내가 댓글 단 사진 조회
     */
    @GetMapping("/photos/commented")
    public List<?> getMyCommentedPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        Long memberId = user.getMemberId();
        log.info("내 댓글 단 사진 요청 by 사용자 ID={}", memberId);

        return myPageService.getMyCommentedPhotos(memberId);
    }
}
