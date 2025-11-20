package org.kosa.congmouse.nyanggoon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.security.jwt.JwtAuthenticationEntryPoint;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.HeritageEncyclopediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HeritageEncyclopediaController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("HeritageEncyclopediaController Unit Test")
public class HeritageEncyclopediaControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private HeritageEncyclopediaService heritageEncyclopediaService;
    @MockitoBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    @DisplayName("GET /heritages/list/name - 로그인 사용자로 문화재(가나다순) 리스트 조회 + 북마크 검증")
    void getHeritageEncyclopediaNameList() throws Exception {
        // given
        int page = 0;
        int size = 4;
        CustomMemberDetails mockMember = new CustomMemberDetails(
                Member.builder()
                        .id(1L)
                        .email("test@test.com")
                        .password("1234")
                        .nickname("test")
                        .phoneNumber("000-0000-0000")
                        .build());
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(mockMember, null,
                        mockMember.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        Page<HeritageEncyclopediaResponseDto> mockPage =
                new PageImpl<>(List.of(
                        HeritageEncyclopediaResponseDto.builder()
                                .id(1L)
                                .name("숭례문")
                                .address("서울특별시")
                                .bookmarkCount(10)
                                .isBookmarked(true)
                                .build())
                );

        // 응답 mock 데이터
        when(heritageEncyclopediaService
                .getAllHeritageEncyclopediasSortedByKoreanName(page, size, mockMember.getMemberId()))
                .thenReturn(mockPage);

        // when & then
        mockMvc.perform(get("/heritages/list/name")
                        .param("page", String.valueOf(page)) // page 파라미터 추가
                        .param("size", String.valueOf(size)) // size 파라미터 추가
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1));

    }

}
