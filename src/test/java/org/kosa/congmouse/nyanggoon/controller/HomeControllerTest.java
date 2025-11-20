package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
@WithMockUser(username = "user1@example.com", roles = {"USER"})  // ✅ 가짜 로그인 사용자
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;   // HTTP 호출을 위한 MockMVC 사용

    @Test
    @DisplayName("도감 북마크 순으로 가져오기")
    public void getHeritageListTest() throws Exception{

        //MockMVC 실행
        ResultActions resultActions = mockMvc.perform(get("/home/heritage")
                .contentType(MediaType.APPLICATION_JSON));

        //응답 확인
        resultActions.andExpect(status().isOk())
                .andDo(print());
    }

}
