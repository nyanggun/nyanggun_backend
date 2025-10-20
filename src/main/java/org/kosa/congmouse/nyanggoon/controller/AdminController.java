package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/reports")
    public ResponseEntity<?> getReportList(){
        List<ReportResponseDto> reportResponseDtoList = adminService.getReportList();
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDtoList, "신고 리스트 조회 완료"));
    }

    @GetMapping("/reports/{id}")
    public ResponseEntity<?> getReport(@PathVariable Long id){
        ReportResponseDto reportResponseDto = adminService.getReport(id);
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDto, "신고 조회 완료"));
    }

    @PatchMapping("/reports/{id}/state")
    public ResponseEntity<?> patchReportState(@PathVariable Long id){
        ReportResponseDto reportResponseDto = adminService.changeReportState(id);
        return ResponseEntity.ok(ApiResponseDto.success(reportResponseDto, "신고 상태 변경 완료"));
    }
}
