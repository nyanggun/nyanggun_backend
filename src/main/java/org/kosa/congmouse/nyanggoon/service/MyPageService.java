package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.ProfilePictureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder passwordEncoder;

    /* =================== 내 정보 조회 =================== */
    public MemberResponseDto getProfileData(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        log.info("회원 정보 조회 완료: ID={}", memberId);
        return MemberResponseDto.from(member);
    }

    /* =================== 내 정보 수정 =================== */
    @Transactional
    public MemberResponseDto updateProfile(Long memberId, MemberUpdateRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

        // 이메일, 닉네임, 전화번호, 비밀번호 업데이트
        String email = dto.getEmail() != null ? dto.getEmail() : member.getEmail();
        String nickname = dto.getNickname() != null ? dto.getNickname() : member.getNickname();
        String phone = dto.getPhoneNumber() != null ? dto.getPhoneNumber() : member.getPhoneNumber();

        String password = dto.getPassword() != null && !dto.getPassword().isBlank()
                ? passwordEncoder.encode(dto.getPassword())
                : member.getPassword();

        memberRepository.updateMemberInfo(memberId, email, nickname, phone, password);

        // 프로필 이미지 처리
        if (dto.getProfileImage() != null) {
            if (!dto.getProfileImage().isBlank()) {
                // 기존 이미지 삭제 후 새로 저장
                profilePictureRepository.deleteByMember(member);

                String path = dto.getProfileImage();
                String originalName = path.substring(path.lastIndexOf('/') + 1);

                ProfilePicture picture = ProfilePicture.builder()
                        .path(path)
                        .originalName(originalName)
                        .savedName(UUID.randomUUID() + "_" + originalName)
                        .size(0L)
                        .fileExtension(getFileExtension(originalName))
                        .build();

                picture.profileOwner(member);
                profilePictureRepository.save(picture);
                log.info("프로필 이미지 수정 완료: {}", path);
            } else {
                // 빈 문자열일 경우 이미지 삭제 요청
                profilePictureRepository.deleteByMember(member);
                log.info("프로필 이미지 삭제 완료: memberId={}", memberId);
            }
        }

        Member updated = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "수정된 회원 정보를 찾을 수 없습니다."));
        return MemberResponseDto.from(updated);
    }

    /* =================== 회원 탈퇴 =================== */
    @Transactional
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "탈퇴할 회원을 찾을 수 없습니다."));
        memberRepository.delete(member);
        log.info("회원 탈퇴 완료: ID={}", memberId);
    }

//    /* =================== 관리자 제재 (상태 변경) =================== */
//    @Transactional
//    public void sanctionMember(Long memberId) {
//        Member member = memberRepository.findById(memberId)
//                .orElseThrow(() -> new ResponseStatusException(
//                        HttpStatus.NOT_FOUND, "제재할 회원을 찾을 수 없습니다."));
//
//        // 현재 상태가 DISABLE인지 먼저 체크
//        if (member.getMemberstate() == MemberState.DISABLE) {
//            log.warn("이미 제재 상태인 회원: {}", memberId);
//            return;
//        }
//
//        // 상태 변경
//        memberRepository.updateMemberState(memberId, MemberState.DISABLE);
//        log.info("회원 제재 완료: ID={}", memberId);
//    }
//
    /* =================== Helper =================== */
    private String getFileExtension(String originalName) {
        if (originalName != null && originalName.contains(".")) {
            String ext = originalName.substring(originalName.lastIndexOf(".") + 1);
            return ext.length() > 10 ? ext.substring(0, 10) : ext.toLowerCase();
        }
        return "";
    }

    /* =================== 탐방기 / 게시글 =================== */

    //    // 내 게시글
    //    public List<ExplorationDetailDto> getMyPosts(Long memberId) {
    //        log.info("내 게시글 조회: userId={}", memberId);
    //        return explorationRepository.findAllByMemberIdWithCounts(memberId);
    //    }
    //
    //    // 내가 북마크한 게시글
    //    public List<ExplorationDetailDto> getMyBookmarks(Long memberId) {
    //        log.info("내 북마크 게시글 조회: userId={}", memberId);
    //        return explorationBookmarkRepository.findExplorationBookmarksByMember(memberId).stream()
    //                .map(ExplorationDetailDto::from)
    //                .toList();
    //    }
    //
    //    // 내가 작성한 댓글 (Talk)
    //    public List<TalkDetailResponseDto> getMyComments(Long memberId) {
    //        log.info("내 댓글 조회: userId={}", memberId);
    //        return talkCommentRepository.findTalkCommentsByMember(memberId).stream()
    //                .map(TalkDetailResponseDto::from)
    //                .toList();
    //    }
    //
    //    // 내가 북마크한 담소
    //    public List<TalkDetailResponseDto> getMyBookmarkedTalks(Long memberId) {
    //        log.info("내 담소 북마크 조회: userId={}", memberId);
    //        return talkBookmarkRepository.findTalkBookmarksByMember(memberId).stream()
    //                .map(TalkDetailResponseDto::from)
    //                .toList();
    //    }
    //
    //    /* =================== 사진 =================== */
    //
    //    // 내가 업로드한 사진
    //    public List<PhotoBoxDetailResponseDto> getMyPhotos(Long memberId) {
    //        log.info("내 사진 조회: userId={}", memberId);
    //        return photoBoxRepository.findPhotoBoxesByMember(memberId).stream()
    //                .map(PhotoBoxDetailResponseDto::from)
    //                .toList();
    //    }
    //
    //    // 내가 북마크한 사진
    //    public List<PhotoBoxDetailResponseDto> getMyBookmarkedPhotos(Long memberId) {
    //        log.info("내 북마크 사진 조회: userId={}", memberId);
    //        return photoBoxRepository.findBookmarkedPhotoBoxesByMember(memberId).stream()
    //                .map(PhotoBoxDetailResponseDto::from)
    //                .toList();
    //    }
    //
    //    // 내가 댓글 단 사진
    //    public List<PhotoBoxDetailResponseDto> getMyCommentedPhotos(Long memberId) {
    //        log.info("내 댓글 단 사진 조회: userId={}", memberId);
    //        return photoBoxRepository.findCommentedPhotoBoxesByMember(memberId).stream()
    //                .map(PhotoBoxDetailResponseDto::from)
    //                .toList();
    //    }
    //
    //    /* =================== 도감 =================== */
    //
    //    // 내가 북마크한 도감
    //    public List<EncyclopediaBookmarkDto> getMyBookmarkedEncyclopedia(Long memberId) {
    //        log.info("내 도감 북마크 조회: userId={}", memberId);
    //        return heritageEncyclopediaRepository.findBookmarksByMember(memberId).stream()
    //                .map(EncyclopediaBookmarkDto::from)
    //                .toList();
    //    }
}