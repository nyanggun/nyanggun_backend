package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.TokenResponse;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.ProfilePictureRepository;

import org.kosa.congmouse.nyanggoon.security.jwt.JwtUtil;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MemberRepository memberRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;  // JsonLoginFilter에서 쓰던 JWT 유틸



    //유저의 정보를 확인하는 메소드 입니다.

    public MemberResponseDto getMemberInfo(Long id){

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        ProfilePicture profilePicture = profilePictureRepository.findById(id).orElse(null);

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImagePath(profilePicture != null ? profilePicture.getPath() : null)
                .phoneNumber(member.getPhoneNumber())
                .build();


        return memberResponseDto;
    }

    //유저의 정보를 수정하는 메소드 입니다.

    @Transactional
    public TokenResponse updateUserInfo(Long id, MemberUpdateRequestDto memberUpdateRequestDto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (!member.getId().equals(id)) {
            throw new AccessDeniedException("회원 정보 수정 권한이 없습니다.");
        }

        if (!passwordEncoder.matches(memberUpdateRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        // DTO 기반 새 객체 생성
        member.updateInfo(
                memberUpdateRequestDto.getEmail(),
                memberUpdateRequestDto.getNickname(),
                memberUpdateRequestDto.getPhoneNumber()
        );

        memberRepository.save(member);

        // 5. 새 JWT 발급 (기존 JsonLoginFilter와 동일 방식)
        long expiredMs = 1000L * 60 * 60 * 24; // 1일
        String newToken = jwtUtil.createJwt(member, expiredMs);

        log.info("회원 정보 수정 완료 및 새 토큰 발급: {}", member.getEmail());

        // 6. 새 토큰 반환
        return new TokenResponse(newToken);

    }

    //회원 탈퇴를 하는 메소드 입니다.
    @Transactional
    public void deleteUserInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        // 권한 체크
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        log.info("username from auth: {}", username);
        log.info("member email: {}", member.getEmail());

        // 본인 또는 관리자면 가능
        if (!member.getEmail().equals(username) && !isAdmin) {
            throw new AccessDeniedException("회원 탈퇴 권한이 없습니다.");
        }

        memberRepository.delete(member);
        log.info("회원 탈퇴 완료: {}", member.getEmail());
    }
}