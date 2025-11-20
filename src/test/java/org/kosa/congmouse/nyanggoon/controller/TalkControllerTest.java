package org.kosa.congmouse.nyanggoon.controller;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.TalkCreateRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "user1@example.com", roles = {"USER"})  // ✅ 가짜 로그인 사용자
public class TalkControllerTest {

    @Autowired
    private MockMvc mockMvc;   // HTTP 호출을 위한 MockMVC 사용

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    @DisplayName("게시글 조회하기")
    public void getAllTalkListTest() throws Exception{

        //파라미터 설정
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("cursor", "");

        //MockMVC 실행
        ResultActions resultActions = mockMvc.perform(get("/talks")
                .params(param)
                .contentType(MediaType.APPLICATION_JSON));

        //응답 확인
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("게시글 작성하기")
    public void createTalkTest() throws Exception {
        // DTO를 JSON으로 변환
        String talkDataJson = objectMapper.writeValueAsString(
                TalkCreateRequestDto.builder()
                        .title("제목이오")
                        .content("내용이오")
                        .build()
        );

        // MockMultipartFile 생성 (DTO만 담음)
        MockMultipartFile talkData = new MockMultipartFile(
                "talkData",           // @RequestPart 이름과 동일
                "talkData.json",      // 파일 이름
                "application/json",   // Content-Type
                talkDataJson.getBytes()
        );

        mockMvc.perform(multipart("/talks")
                        .file(talkData)
                        .with(request -> { request.setMethod("POST"); return request; }) // POST로 강제
                )
                .andExpect(status().isCreated())  // 201 CREATED 확인
                .andDo(print());
    }

}
