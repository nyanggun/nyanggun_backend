package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Report;
import org.kosa.congmouse.nyanggoon.entity.ReportState;
import org.kosa.congmouse.nyanggoon.repository.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final ReportRepository reportRepository;

    public List<ReportResponseDto> getReportList() {
        List<Report> reportList = reportRepository.findAll();
        List<ReportResponseDto> reportResponseDtoList = reportList.stream().map(ReportResponseDto::from).toList();
        return reportResponseDtoList;
    }

    public ReportResponseDto getReport(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(()->{
            throw new RuntimeException("해당하는 신고가 존재하지 않습니다");
        });
        return ReportResponseDto.from(report);
    }

    @Transactional
    public ReportResponseDto changeReportState(Long id) {
        Report report = reportRepository.findById(id).orElseThrow(()->{
            throw new RuntimeException("해당하는 게시물이 존재하지 않습니다");
        });
        report.changeState(ReportState.PROCESSED);
        return ReportResponseDto.from(report);
    }
}
