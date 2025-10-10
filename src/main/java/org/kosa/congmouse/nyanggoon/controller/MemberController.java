package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberRegisterDto;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    public ResponseEntity<?> postMember(@RequestBody MemberRegisterDto memberRegisterDto){
        MemberResponseDto responseDto = memberService.registerMember(memberRegisterDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(responseDto, "회원가입이 완료되었습니다."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMyInfo() {
        log.info("=== 내 정보 조회 요청 ===");
        // SecurityContext에서 현재 인증된 사용자 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("인증된 사용자 정보 조회: email={}", email);
        // 예외는 GlobalExceptionHandler가 처리
        MemberResponseDto memberInfo = memberService.getMyInfo(email);
        return ResponseEntity.ok(ApiResponseDto.success(memberInfo, "회원 정보 조회 OK"));
    }
}
