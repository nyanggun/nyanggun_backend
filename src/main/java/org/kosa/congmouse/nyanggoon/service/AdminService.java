package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ReportResponseDto;
import org.kosa.congmouse.nyanggoon.entity.*;
import org.kosa.congmouse.nyanggoon.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class AdminService {
    private final ReportRepository reportRepository;
    private final TalkRepository talkRepository;
    private final ExplorationRepository explorationRepository;
    private final TalkCommentRepository talkCommentRepository;
    private final ExplorationCommentRepository explorationCommentRepository;
    private final PhotoBoxRepository photoBoxRepository;

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

    @Transactional
    public ReportResponseDto changeContentState(Long id){

    Report report = reportRepository.findById(id).orElseThrow(()-> {
        throw new RuntimeException("해당하는 게시물이 존재하지 않습니다");
    });

    if(report.getContentType() == ContentType.TALK) {
        Talk talk = talkRepository.findById(report.getContentId()).orElseThrow(() -> {
            throw new RuntimeException("해당하는 게시물이 존재하지 않습니다");
        });
        talk.changeState(ContentState.INACTIVE);

    }else if (report.getContentType()== ContentType.EXPLORATION){
        Exploration exploration = explorationRepository.findById(report.getContentId()).orElseThrow(() -> {
            throw new RuntimeException("해당하는 게시물이 존재하지 않습니다");
        });
       exploration.changeState(ContentState.INACTIVE);

    }else if (report.getContentType()== ContentType.PHOTO_BOX) {
        PhotoBox photoBox = photoBoxRepository.findById(report.getContentId()).orElseThrow(() -> {
            throw new RuntimeException("해당하는 게시물이 존재하지 않습니다");
        });
        photoBox.changeState(ContentState.INACTIVE);

    }else if(report.getContentType()== ContentType.TALK_COMMENT){
        TalkComment talkComment = talkCommentRepository.findById(report.getContentId()).orElseThrow(() -> {
            throw new RuntimeException("해당하는 댓글이 존재하지 않습니다");
        });
        talkComment.changeState(ContentState.INACTIVE);
    }else {
        ExplorationComment explorationComment = explorationCommentRepository.findById(report.getContentId()).orElseThrow(() -> {
            throw new RuntimeException("해당하는 댓글이 존재하지 않습니다");
        });
        explorationComment.changeState(ContentState.INACTIVE);
    }

    //마지막으로 처리 상태도 변경해준다 -> 처리 완료로
        report.changeState(ReportState.PROCESSED);

    return ReportResponseDto.from(report);
    }
}
