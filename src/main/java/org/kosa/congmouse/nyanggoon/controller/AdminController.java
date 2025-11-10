package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.service.AdminService;
import org.kosa.congmouse.nyanggoon.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name="관리자 기능", description = "관리자 관련 기능 Controller")
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;
    private final MemberService memberService;

    @Operation(summary="신고 리스트 조회", description = "멤버가 작성한 신고 리스트를 조회한다")
    @GetMapping("/reports")
    public ResponseEntity<?> getReportList(){
        List<ReportResponseDto> reportResponseDtoList = adminService.getReportList();
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDtoList, "신고 리스트 조회 완료"));
    }

    @Operation(summary="개별 신고 조회", description = "멤버가 작성한 개별 신고를 조회한다")
    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReport(@Parameter(description="신고 id", example="1")@PathVariable Long id){
        ReportResponseDto reportResponseDto = adminService.getReport(id);
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDto, "신고 조회 완료"));
    }

    @Operation(summary="개별 신고 처리", description = "멤버가 작성한 개별 신고의 상태를 변경한다")
    @PatchMapping("/reports/{id}/state")
    public ResponseEntity<?> patchReportState(@Parameter(description="신고 id", example="1")@PathVariable Long id){
        ReportResponseDto reportResponseDto = adminService.changeReportState(id);
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDto, "신고 상태 변경 완료"));
    }

    @Operation(summary="멤버 리스트 조회", description = "전체 멤버 리스트를 조회한다")
    @GetMapping("/members")
    public ResponseEntity<?> getUserList(){
        List<MemberResponseDto> memberResponseDtoList = memberService.findAllMembers();
        return ResponseEntity.ok(ApiResponseDto.success(memberResponseDtoList, "유저 리스트 조회 완료"));
    }

    @Operation(summary="개별 멤버 상태 변경", description = "개별 멤버의 상태를 변경한다")
    @PatchMapping("/members/{id}/state")
    public ResponseEntity<?> patchUserState(@Parameter(description="멤버 id", example="1") @PathVariable Long id){
        MemberResponseDto memberResponseDto = memberService.changeUserState(id);
        return ResponseEntity.ok(ApiResponseDto.success(memberResponseDto, "유저 상태 변경 완료"));
    }
}
