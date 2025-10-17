package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.entity.ExplorationReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ExplorationReportService explorationReportService;

    @GetMapping("/reports/explorations")
    public ResponseEntity<?> getExplorationReportList(){
        List<ReportResponseDto> explorationReportList = explorationReportService.getExplorationReportList();
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportList, "문화재 탐방기 신고 조회 완료"));
    }
}
