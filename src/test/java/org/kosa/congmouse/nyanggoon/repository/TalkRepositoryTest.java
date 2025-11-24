package org.kosa.congmouse.nyanggoon.repository;

import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.TalkCreateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkPicture;
import org.kosa.congmouse.nyanggoon.service.TalkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class TalkRepositoryTest {

    @Autowired
    TalkService talkService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    TalkRepository talkRepository;

    @Autowired
    TalkPictureRepository talkPictureRepository;


    //'담소를 저장하는 테스트 입니다.
    @Test
    public void saveTalkTest() {
        Member member = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        Talk talk = Talk.builder().title("제목").content("내용").member(member).build();
        Talk saved = talkRepository.save(talk);

        Talk find = talkRepository.findById(saved.getId()).orElseThrow();

        assertEquals("제목", find.getTitle());
    }
}
