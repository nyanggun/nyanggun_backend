package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.TokenResponse;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.MemberService;
import org.kosa.congmouse.nyanggoon.service.MyPageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final MemberService memberService;

    /**
     * 유저의 정보를 가져오는 컨트롤러 입니다.
     * 본인 정보/다른 유저 조회 용으로도 사용 가능합니다.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserInfo(@PathVariable Long id){

        MemberResponseDto memberResponseDto = myPageService.getMemberInfo(id);

        return ResponseEntity.ok(ApiResponseDto.success(memberResponseDto , "회원 정보 조회 성공"));

    }


    /**
     * 내 정보를 수정하는 컨트롤러 입니다.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUserInfo(@PathVariable Long id, @RequestBody MemberUpdateRequestDto memberUpdateRequestDto){

        TokenResponse token = myPageService.updateUserInfo(id, memberUpdateRequestDto);

        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 정보 수정 성공"));

    }

    /**
     * 회원 탈퇴하는 컨트롤러 입니다.
     */

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserInfo(@PathVariable Long id){
        myPageService.deleteUserInfo(id);
        return ResponseEntity.ok(ApiResponseDto.success(null , "회원 탈퇴 성공"));

    }

    /**
     * 내 게시글을 조회하는 컨트롤러 입니다.
     */

}