package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Report;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long id;
    private String contentType;
    private Long contentId;
    private Long reportMemberId;
    private String reason;
    private String reportState;
    private LocalDateTime createdAt;

    public static ReportResponseDto from(Report report){
        return ReportResponseDto.builder()
                .id(report.getId())
                .createdAt(report.getCreatedAt())
                .reason(report.getReason())
                .reportState(report.getReportState().toString())
                .contentType(report.getContentType().toString())
                .contentId(report.getContentId())
                .reportMemberId(report.getReportMember().getId())
                .build();
    }
}
