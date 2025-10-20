package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class MyPageController {

    private final MyPageService myPageService;

    /* 내 정보 조회 - 로그인 필수 */
    @GetMapping("/{id}")
    public ResponseEntity<?> getProfile(@PathVariable Long id) {
        CustomMemberDetails user = getAuthenticatedUser(); // 로그인 필수
        if (!user.getMemberId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 계정 정보만 조회 가능합니다.");
        }

        MemberResponseDto profile = myPageService.getProfileData(id);
        return ResponseEntity.ok(profile);
    }

    /* 내 정보 수정 - 로그인 필수 */
    @PutMapping("/profileupdate/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody MemberUpdateRequestDto dto) {
        CustomMemberDetails user = getAuthenticatedUser();
        if (!user.getMemberId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 계정만 수정할 수 있습니다.");
        }

        MemberResponseDto updatedProfile = myPageService.updateProfile(id, dto);
        return ResponseEntity.ok(updatedProfile);
    }

    /* 회원 탈퇴 - 로그인 필수 */
    @DeleteMapping("/delete/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMember(@PathVariable Long id) {
        CustomMemberDetails user = getAuthenticatedUser();
        if (!user.getMemberId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "본인 계정만 탈퇴할 수 있습니다.");
        }

        myPageService.deleteMember(id);
    }

    /* ===== Helper ===== */
    private CustomMemberDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
        }
        return (CustomMemberDetails) authentication.getPrincipal();
    }

//    /* 회원 제재 (관리자 전용) */
//    @PutMapping("/sanction/{id}")
//    public ResponseEntity<?> sanctionMember(@PathVariable Long id) {
//        CustomMemberDetails user = getAuthenticatedUser();
//        if (!user.getMember().getRole().toString().equals("ROLE_ADMIN")) {
//            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "관리자 권한이 필요합니다.");
//        }
//        myPageService.sanctionMember(id);
//        return ResponseEntity.ok("회원 제재 완료: ID=" + id);
//    }

//    /**
//     * 내가 작성한 게시글 목록 조회
//     */
//    @GetMapping("/posts")
//    public List<?> getMyPosts(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 게시글 목록 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyPosts(memberId);
//    }
//
//    /**
//     * 내가 북마크한 게시글 조회
//     */
//    @GetMapping("/bookmarks")
//    public List<?> getMyBookmarks(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 북마크 게시글 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyBookmarks(memberId);
//    }
//
//    /**
//     * 내가 작성한 댓글 목록 조회
//     */
//    @GetMapping("/comments")
//    public List<?> getMyComments(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 댓글 목록 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyComments(memberId);
//    }
//
//    /**
//     * 내가 업로드한 사진 조회
//     */
//    @GetMapping("/photos")
//    public List<?> getMyPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 사진 목록 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyPhotos(memberId);
//    }
//
//    /**
//     * 내가 북마크한 사진 조회
//     */
//    @GetMapping("/photos/bookmarked")
//    public List<?> getMyBookmarkedPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 북마크 사진 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyBookmarkedPhotos(memberId);
//    }
//
//    /**
//     * 내가 댓글 단 사진 조회
//     */
//    @GetMapping("/photos/commented")
//    public List<?> getMyCommentedPhotos(@AuthenticationPrincipal CustomMemberDetails user) {
//        if (user == null) {
//            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인 후 이용해주세요.");
//        }
//        Long memberId = user.getMemberId();
//        log.info("내 댓글 단 사진 요청 by 사용자 ID={}", memberId);
//
//        return myPageService.getMyCommentedPhotos(memberId);
//    }
}