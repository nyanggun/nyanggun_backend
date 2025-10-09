package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.MemberRegisterDto;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public MemberResponseDto registerMember(MemberRegisterDto memberRegisterDto){
        if(memberRepository.existsByEmail(memberRegisterDto.getEmail())){
            log.warn("회원가입 실패 - 중복된 username: {}", memberRegisterDto.getEmail());
            throw new IllegalArgumentException("이미 사용 중인 email 입니다.");
        }

        Member member = Member.builder()
                        .email(memberRegisterDto.getEmail())
                        .password(passwordEncoder.encode(memberRegisterDto.getPassword()))
                        .nickname(memberRegisterDto.getNickname())
                        .phoneNumber((memberRegisterDto.getPhoneNumber()))
                        .build();

        Member savedMember = memberRepository.save(member);
        log.info("회원가입 성공: email={}, nickname={}", savedMember.getEmail(), savedMember.getNickname());

        return MemberResponseDto.from(member);
    }
}
