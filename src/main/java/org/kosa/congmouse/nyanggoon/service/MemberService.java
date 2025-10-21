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

import java.util.List;

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

    public MemberResponseDto findByUsername(String email) {
        log.debug("회원 조회: email={}", email);

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> {
                    log.error("회원 조회 실패: username={}", email);
                    return new IllegalArgumentException("존재하지 않는 회원입니다.");
                });

        return MemberResponseDto.from(member);
    }


    public MemberResponseDto getMyInfo(String email) {
        log.debug("내 정보 조회: email={}", email);
        return findByUsername(email);
    }

    public List<MemberResponseDto> findAllMembers(){
        List<Member> memberList = memberRepository.findAll();
        List<MemberResponseDto> memberResponseDtoList = memberList.stream().map(MemberResponseDto::from).toList();
        return memberResponseDtoList;
    }

    @Transactional
    public MemberResponseDto changeUserState(Long id) {
        Member member = memberRepository.findById(id).orElseThrow(()->{
            throw new RuntimeException("해당하는 멤버가 존재하지 않습니다");
        });
        member.changeMemberState();
        return MemberResponseDto.from(member);
    }
}
