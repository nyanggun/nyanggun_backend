package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.Field;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberResponseDto getProfileData(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

        log.info("회원 기본 정보 조회 완료: userId={}", memberId);
        return MemberResponseDto.from(member);
    }

    @Transactional
    public MemberResponseDto updateProfile(Long memberId, MemberUpdateRequestDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "회원 정보를 찾을 수 없습니다."));

        try {
            if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
                setPrivateField(member, "email", dto.getEmail());
            }
            if (dto.getNickname() != null && !dto.getNickname().isBlank()) {
                setPrivateField(member, "nickname", dto.getNickname());
            }
            if (dto.getPhoneNumber() != null && !dto.getPhoneNumber().isBlank()) {
                setPrivateField(member, "phoneNumber", dto.getPhoneNumber());
            }
            if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
                setPrivateField(member, "password", passwordEncoder.encode(dto.getPassword()));
            }
            if (dto.getProfileImage() != null && !dto.getProfileImage().isBlank()) {
                ProfilePicture profilePicture = new ProfilePicture();
                setPrivateField(member, "profilePicture", profilePicture);
            }

            log.info("회원 정보 수정 완료: userId={}", memberId);
            return MemberResponseDto.from(member);

        } catch (Exception e) {
            log.error("회원 정보 수정 중 오류 발생: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "회원 정보 수정 중 오류 발생");
        }
    }

    // 리플렉션을 이용해 private 필드 값 수정하는 헬퍼 메서드
    private void setPrivateField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true); // private 접근 허용
        field.set(target, value);
    }
}
