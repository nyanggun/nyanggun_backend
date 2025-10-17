package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ReportCreateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.dto.TalkReportCreateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.ExplorationReport;
import org.kosa.congmouse.nyanggoon.repository.ExplorationReportRepository;
import org.kosa.congmouse.nyanggoon.repository.ExplorationRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExplorationReportService {
    private final ExplorationReportRepository explorationReportRepository;
    private final ExplorationRepository explorationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public ReportResponseDto createExplorationReport(ReportCreateRequestDto explorationReportCreateRequestDto) {
        ExplorationReport newExplorationReport = ExplorationReport.builder()
                .reason(explorationReportCreateRequestDto.getReason())
                .exploration(explorationRepository.findById(explorationReportCreateRequestDto.getPostId())
                        .orElseThrow(()-> {
                            throw new RuntimeException("memberId에 해당하는 member가 존재하지 않습니다");
                        }))
                .member(memberRepository.findById(explorationReportCreateRequestDto.getMemberId())
                        .orElseThrow(() -> {
                            throw new RuntimeException("explorationId 해당하는 exploration이 존재하지 않습니다");
                        }))
                .build();
        ExplorationReport resultExplorationReport = explorationReportRepository.save(newExplorationReport);
        return ReportResponseDto.fromExploration(resultExplorationReport);
    }

    @Transactional
    public void createTalkReport(TalkReportCreateRequestDto talkReportCreateRequestDto){

    }

    public List<ReportResponseDto> getExplorationReportList() {
        List<ExplorationReport> explorationReportList = explorationReportRepository.findAll();
        List<ReportResponseDto> explorationReportResponseDtoList = explorationReportList.stream().map(
                (explorationReport) -> ReportResponseDto.fromExploration(explorationReport)
        ).toList();
        return explorationReportResponseDtoList;
    }
}
