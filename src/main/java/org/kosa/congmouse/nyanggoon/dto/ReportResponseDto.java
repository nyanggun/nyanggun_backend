package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.ExplorationReport;
import org.kosa.congmouse.nyanggoon.entity.TalkReport;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponseDto {
    private Long id;
    private LocalDateTime createdAt;
    private String reason;
    private String reportState;
    private Long postId;
    private Long memberId;

    public static ReportResponseDto fromExploration(ExplorationReport explorationReport){
        return ReportResponseDto.builder()
                .id(explorationReport.getId())
                .createdAt(explorationReport.getCreatedAt())
                .reason(explorationReport.getReason())
                .reportState(explorationReport.getReportState().toString())
                .postId(explorationReport.getExploration().getId())
                .memberId(explorationReport.getMember().getId())
                .build();
    }

    public static ReportResponseDto fromTalkReport(TalkReport talkReport) {
        return ReportResponseDto.builder()
                .id(talkReport.getId())
                .createdAt(talkReport.getCreatedAt())
                .reason(talkReport.getReason())
                .reportState(talkReport.getReportState().toString())
                .postId(talkReport.getTalk().getId())
                .memberId(talkReport.getMember().getId())
                .build();
    }
}
