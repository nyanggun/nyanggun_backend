package org.kosa.congmouse.nyanggoon.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.ReportCreateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.TalkCreateResponseDto;
import org.kosa.congmouse.nyanggoon.dto.TalkCursorResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.Report;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.ReportRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

import java.util.List;


import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
public class TalkServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TalkRepository talkRepository;

    @Autowired
    private TalkService talkService;

    @Autowired
    private ReportRepository reportRepository;

        @Test
        //담소 게시글 조회 테스트
        void findAllTalkListTest() {
            // given
            Member member = memberRepository.findByEmail("user1@example.com")
                    .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

            // 더미 Talk 엔티티
            Talk talk1 = Talk.builder()
                    .title("첫 번째 담소")
                    .content("담소 내용임")
                    .member(member)
                    .talkPictures(new ArrayList<>()) // 추가
                    .build();
            // 더미 Talk 엔티티2
            Talk talk2 = Talk.builder()
                    .title("두 번째 담소")
                    .content("담소 내용입니다.")
                    .member(member)
                    .talkPictures(new ArrayList<>()) // 추가
                    .build();
            talkRepository.save(talk1);
            talkRepository.save(talk2);


            // when
            TalkCursorResponseDto<List<TalkCreateResponseDto>> response =
                    talkService.findAllTalkList("user1@example.com", null);

            // then
            assertNotNull(response);
            Assertions.assertEquals(2, response.getContents().size());

        }

        @Test
    //담소 게시글 삭제 테스트
        void createTalkTest() {
            // given
            Member member = memberRepository.findByEmail("user1@example.com")
                    .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

            // 더미 Talk 엔티티
            Talk talk = Talk.builder()
                    .title("첫 번째 담소")
                    .content("담소 내용임")
                    .member(member)
                    .talkPictures(new ArrayList<>()) // 추가
                    .build();

            Talk savedTalk = talkRepository.save(talk);
            Long talkId = savedTalk.getId();

            // when
            talkService.deleteTalk(talkId);

            // then
            boolean exists = talkRepository.findById(talkId).isPresent();
            Assertions.assertFalse(exists, "삭제 후에는 게시글이 존재하지 않아야 합니다.");

        }

        //담소 게시글 신고 테스트
        @Test
        void reportTalkTest() {
            // given
            Member member = memberRepository.findByEmail("user1@example.com")
                    .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

            // 더미 Talk 엔티티
            Talk talk = Talk.builder()
                    .title("첫 번째 담소")
                    .content("담소 내용임")
                    .member(member)
                    .talkPictures(new ArrayList<>()) // 추가
                    .build();

            Talk savedTalk = talkRepository.save(talk);
            Long talkId = savedTalk.getId();

            // when
            ReportCreateRequestDto report = ReportCreateRequestDto.builder()
                    .postId(talkId)
                    .reason("신고 내용임")
                    .memberId(member.getId())
                    .build();

            talkService.createTalkReport(report);

            // then
            List<Report> reports = reportRepository.findAll();
            Assertions.assertEquals(1, reports.size(), "신고가 1개 저장되어야 합니다.");
            Report savedReport = reports.get(0);
            Assertions.assertEquals("신고 내용임", savedReport.getReason());
            Assertions.assertEquals(member.getId(), savedReport.getReportMember().getId());
        }
}
