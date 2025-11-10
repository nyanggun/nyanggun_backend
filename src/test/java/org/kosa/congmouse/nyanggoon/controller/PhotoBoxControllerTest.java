package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.ReportCreateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.PhotoBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "user1@example.com", roles = {"USER"})  // ✅ 가짜 로그인 사용자
public class PhotoBoxControllerTest {

    @Autowired
    private MockMvc mockMvc;   // HTTP 호출을 위한 MockMVC 사용

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PhotoBoxRepository photoBoxRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("사진함 게시글 조회하기")
    public void getAllPhotoBoxListTest() throws Exception{

        //파라미터 설정
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("cursor", "");

        //MockMVC 실행
        ResultActions resultActions = mockMvc.perform(get("/photobox")
                .params(param)
                .contentType(MediaType.APPLICATION_JSON));

        //응답 확인
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("사진함 게시글 신고하기")
    @Transactional  // 테스트 후 데이터 롤백
    public void postPhotoBoxReportTest() throws Exception {

        Member testMember = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));


        // 테스트용 게시글 생성 (ID 1이라고 가정)
        PhotoBox testPost = PhotoBox.builder()
                .title("테스트 게시글")
                .tags(new ArrayList<>())
                .relatedHeritage("숭례문")
                .member(testMember)
                .build();
        photoBoxRepository.save(testPost);  // 저장하면 자동으로 ID 할당

        // 요청 DTO 생성 (실제 ID로 맞춤)
        ReportCreateRequestDto requestDto = ReportCreateRequestDto.builder()
                .postId(testPost.getId())  // 여기서 실제 저장된 ID 사용
                .memberId(testMember.getId())
                .reason("부적절한 내용입니다;")
                .build();

        // 4. JSON으로 변환
        String json = objectMapper.writeValueAsString(requestDto);

        // 5. MockMvc 수행
        mockMvc.perform(post("/photobox/reports")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andDo(print());
    }

}
