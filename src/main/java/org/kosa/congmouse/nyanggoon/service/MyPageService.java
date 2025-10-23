package org.kosa.congmouse.nyanggoon.service;

import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.MemberState;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.ProfilePictureRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MemberRepository memberRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder passwordEncoder;
    private final String defaultUploadDir = System.getProperty("user.dir") + "/uploads/profilepicture/";

    /* =================== 내 정보 조회 =================== */
    public MemberResponseDto getProfileData(Long memberId) {
        Member updated = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        log.info("회원 정보 조회 완료: ID={}", memberId);

        return MemberResponseDto.from(updated);
    }

    @Transactional
    public MemberResponseDto updateProfile(Long memberId, MemberUpdateRequestDto dto, MultipartFile profileImage) {
        log.info("updateProfile 시작: memberId={}", memberId);
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));
        log.info("회원조회 완료");
        // 1. 회원 기본 정보 업데이트
        // DTO 값이 있으면 사용, 없으면 기존 값 유지
        String newNickname = dto.getNickname() != null ? dto.getNickname() : member.getNickname();
        String newPhone = dto.getPhoneNumber() != null ? dto.getPhoneNumber() : member.getPhoneNumber();
        String newPassword = null;

        // 비밀번호가 입력되었으면 인코딩
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        log.info("회원 정보 업데이트 시작");
        member.changeMemberInfo(dto);

        log.info("회원 기본 정보 업데이트: ID={}, 닉네임={}", memberId, newNickname);

        // ------------------ 2. 프로필 이미지 처리 ------------------
        if (profileImage != null && !profileImage.isEmpty()) {
            try {
                // 기존 이미지 삭제
                profilePictureRepository.findByMember(member).ifPresent(existingPicture -> {
                    File file = new File(defaultUploadDir + existingPicture.getSavedName());
                    if (file.exists()) file.delete();
                    profilePictureRepository.delete(existingPicture);
                });

                // 새 ProfilePicture 생성 및 세팅
                ProfilePicture newPicture = new ProfilePicture();
                newPicture.setFirstProfilePicture(profileImage, member);

                // 파일 서버에 저장
                File uploadsProfilepictureDir = new File(defaultUploadDir);
                if(!uploadsProfilepictureDir.exists()){
                    uploadsProfilepictureDir.mkdirs();
                }
                profileImage.transferTo(new File(defaultUploadDir + newPicture.getSavedName()));
//                Path filePath = Paths.get(defaultUploadDir + newPicture.getPath());
//                if (!Files.exists(filePath.getParent())) Files.createDirectories(filePath.getParent());
//                profileImage.transferTo(filePath.toFile());

                // DB 저장 및 Member에 연결
                profilePictureRepository.save(newPicture);
                member.setProfilePicture(newPicture);

                log.info("새 프로필 이미지 저장 완료: {}", newPicture.getPath());
            } catch (IOException e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "파일 저장에 실패했습니다.", e);
            }
        }else {
            log.info("프로필 이미지 없음");
        }
        MemberResponseDto dtoResult = MemberResponseDto.from(member);
        return dtoResult;
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

    /* =================== Helper =================== */
    private String getFileExtension(String originalName) {
        if (originalName != null && originalName.contains(".")) {
            String ext = originalName.substring(originalName.lastIndexOf(".") + 1);
            return ext.length() > 10 ? ext.substring(0, 10) : ext.toLowerCase();
        }
        return "";
    }

    /* =================== 관리자 제재 (상태 변경) =================== */
    @Transactional
    public void sanctionMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "제재할 회원을 찾을 수 없습니다."));

        // 현재 상태가 DISABLE인지 먼저 체크
        if (member.getMemberstate() == MemberState.DISABLED) {
            log.warn("이미 제재 상태인 회원: {}", memberId);
            return;
        }

        // 상태 변경
        memberRepository.updateMemberState(memberId, MemberState.DISABLED);
        log.info("회원 제재 완료: ID={}", memberId);
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