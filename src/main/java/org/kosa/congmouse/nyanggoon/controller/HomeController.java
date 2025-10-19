package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.service.HomeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/home")
@RequiredArgsConstructor
@Slf4j
public class HomeController {

    private final HomeService homeService;
    //메인에서 북마크 정보로 도감 정보를 가져옵니다.
    //현재는 북마크 수(인기 순)로 도감 정보 4개를 가져옵니다. (=비회원일 때 보이는 화면)
    @GetMapping("/heritage")
    public ResponseEntity<?> getEncyclopediaByBookmark(){

        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        List<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtoList = homeService.getEncyclopediaByBookmark(username);

        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtoList, "메인 도감 정보 조회 성공"));
    }

    //메인에서 사진함 이미지를 띄워줍니다.
    //북마크 수가 가장 높은 것을 띄워줍니다.
    @GetMapping("/photobox")
    public ResponseEntity<?> getPhotoBoxByBookmark(){
        PhotoBoxDetailResponseDto photoBoxDetailResponseDto = homeService.getPhotoBoxByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxDetailResponseDto, "메인 사진함 조회 성공"));

    }

    //메인에서 탐방기 게시글을 띄워줍니다.
    //북마크 수가 가장 높은 게시글 4개를 띄워줍니다.
    @GetMapping("/exploration")
    public ResponseEntity<?> getExplorationByBookmark(){
        List<ExplorationDetailDto> explorationDetailDtoList = homeService.getExplorationByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDtoList, "메인 탐방기 정보 조회 성공"));
    }
}
