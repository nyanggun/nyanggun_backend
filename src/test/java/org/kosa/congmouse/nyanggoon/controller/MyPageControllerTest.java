package org.kosa.congmouse.nyanggoon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
@SpringBootTest
public class MyPageControllerTest {

    @Autowired
    private MockMvc mockMvc;   // HTTP 호출을 위한 MockMVC 사용

    @Test
    @DisplayName("마이페이지 게시글 조회하기")
    public void getAllPhotoBoxListTest() throws Exception{

        //파라미터 설정
        MultiValueMap<String, String> param = new LinkedMultiValueMap<>();
        param.add("cursor", "");

        //MockMVC 실행
        ResultActions resultActions = mockMvc.perform(get("/mypage/{id}/post", 7L)
                .params(param)
                .contentType(MediaType.APPLICATION_JSON));

        //응답 확인
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }
}
