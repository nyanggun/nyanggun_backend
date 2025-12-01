package org.kosa.congmouse.nyanggoon.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
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
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/*
    1. 인증 없는 일반 GET API 테스트
        -> GET /list/name
        -> RequestParam + Pagination + Service 호출 검증
    2. 인증 없는 PathVariable + JSON 응답 구조 테스트
        -> GET /detail/{id}
    3. 인증 있는 POST API 테스트
        -> POST /bookmark/{id}
        -> @AuthenticationPrincipal 또는 ROLE 처리 테스트
*/
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

    // 1. 인증 없는 일반 GET API 테스트
    @Test
    @DisplayName("GET /heritages/list/name - 비로그인 사용자 문화재(가나다순) 리스트 조회")
    void getHeritageEncyclopediaNameListTest() throws Exception {
        // given
        HeritageEncyclopediaResponseDto dto1 = HeritageEncyclopediaResponseDto.builder()
                .id(1L)
                .name("창덕궁")
                .build();
        HeritageEncyclopediaResponseDto dto2 = HeritageEncyclopediaResponseDto.builder()
                .id(2L)
                .name("창경궁")
                .build();
        Page<HeritageEncyclopediaResponseDto> mockPage = new PageImpl<>(List.of(dto1, dto2));

        given(heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByKoreanName(0, 4, null))
                .willReturn(mockPage);

        // when & then
        mockMvc.perform(get("/heritages/list/name")
                .param("page", "0")
                .param("size", "4")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.content[0].name").value("창덕궁"))
                .andExpect(jsonPath("$.data.content[1].name").value("창경궁"))
                .andExpect(jsonPath("$.message").value("문화재 도감 목록 조회 성공"));
    }

    // 2. 인증 없는 PathVariable + JSON 응답 구조 테스트
    @Test
    @DisplayName("GET heritages/detail/{heritageEncyclopediaId} - 비로그인 사용자 문화재 상세 조회")
    void getHeritageEncyclopediaDetailTest() throws Exception {
        // given
        Long id = 5L;
        HeritageEncyclopediaResponseDto responseDto = HeritageEncyclopediaResponseDto.builder()
                .id(id)
                .name("경복궁")
                .build();

        given(heritageEncyclopediaService.getHeritageEncyclopediaById(id, null))
                .willReturn(responseDto);

        // when & then
        mockMvc.perform(get("/heritages/detail/{id}", id)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("success").value(true))
                .andExpect(jsonPath("$.data.content[0].id").value(id))
                .andExpect(jsonPath("$.data.content[0].name").value("경복궁"))
                .andExpect(jsonPath("$.message").value("문화재 조회 성공"));

    }

    // 3. 인증 있는 POST API 테스트
    @Test
    @DisplayName("POST /heritages/bookmark/{id} - 로그인 사용자 북마크 저장")
    void postBookmark() throws Exception {
        // given
        Long heritageId = 7L;
        Long memberId = 3L;

        // 로그인 유저 Security 세팅
        Member mockMember = Member.builder()
                .id(memberId)
                .email("test@test.com")
                .password("password")
                .build();

        CustomMemberDetails user = new CustomMemberDetails(mockMember);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user,
                null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // 테스트 위한 service 반환
        HeritageEncyclopedia mockHeritage = HeritageEncyclopedia.builder()
                .id(heritageId)
                .name("경복궁")
                .build();
        EncyclopediaBookmarkDto bookmarkDto = EncyclopediaBookmarkDto.builder()
                .heritageEncyclopedia(mockHeritage)
                .member(mockMember)
                .build();

        given(heritageEncyclopediaService.saveBookmark(heritageId, memberId))
                .willReturn(bookmarkDto);

        // when & then
        mockMvc.perform(post("/heritages/bookmark/{id}", heritageId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("북마크 등록 성공"))
                .andExpect(jsonPath("$.data.member.id").value(memberId))
                .andExpect(jsonPath("$.data.heritageEncyclopedia.id").value(heritageId))
                .andExpect(jsonPath("$.data.heritageEncyclopedia.name").value("경복궁"));

        verify(heritageEncyclopediaService).saveBookmark(heritageId, memberId);
    }
}
