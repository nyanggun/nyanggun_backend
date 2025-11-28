package org.kosa.congmouse.nyanggoon.service;

import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxCreateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class MyPageServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MyPageService myPageService;

    //내정보를 수정하는 서비스 테스트
    @Test
    public void updatePhotoBoxTest(){
        // given

        Long id = 9L;

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        //when

        MemberUpdateRequestDto memberUpdateRequestDto = MemberUpdateRequestDto.builder().email("user1@example.com")
                .nickname("문화재 사랑단이오")
                .phoneNumber("010-1234-1234")
                .password("1234")
                .build();


        myPageService.updateUserInfo(id, memberUpdateRequestDto);

        //then
        Member updated = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        assertEquals("user1@example.com", updated.getEmail());
        assertEquals("문화재 사랑단이오", updated.getNickname());
        assertEquals("010-1234-1234", updated.getPhoneNumber());

    }
}
