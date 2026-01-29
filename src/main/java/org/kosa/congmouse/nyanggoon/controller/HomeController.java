package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
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
@Tag(name = "메인", description = "메인 화면 컨트롤러 입니다.")
public class HomeController {

    private final HomeService homeService;
    //메인에서 북마크 정보로 도감 정보를 가져옵니다.
    //현재는 북마크 수(인기 순)로 도감 정보 4개를 가져옵니다. (=비회원일 때 보이는 화면)
    @GetMapping("/heritage")
    @Operation(summary = "북마크 수(인기 순)로 도감 정보 4개를 가져오는 컨트롤러", description = "메인에서 북마크 정보로 도감 정보를 가져옵니다. 만약 도감의 북마크 수가 0이라면 랜덤으로 가져옵니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 메인에 도감을 가져왔습니다."))
    public ResponseEntity<?> getEncyclopediaByBookmark(){
        List<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtoList = homeService.getEncyclopediaByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtoList, "메인 도감 정보 조회 성공"));
    }

    //메인에서 사진함 이미지를 띄워줍니다.
    //북마크 수가 가장 높은 것을 띄워줍니다.
    @GetMapping("/photobox")
    @Operation(summary = "북마크 수가 가장 높은 사진함 사진을 가져오는 컨트롤러", description = "메인에서 사진함 이미지를 띄워줍니다. 만약 사진함 사진의 북마크 수가 0이라면 가장 최신의 사진함 사진을 가져옵니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 메인에 사진함 이미지를 가져왔습니다."))
    public ResponseEntity<?> getPhotoBoxByBookmark(){
        PhotoBoxDetailResponseDto photoBoxDetailResponseDto = homeService.getPhotoBoxByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxDetailResponseDto, "메인 사진함 조회 성공"));

    }

    //메인에서 탐방기 게시글을 띄워줍니다.
    //북마크 수가 가장 높은 게시글 4개를 띄워줍니다.
    @GetMapping("/exploration")
    @Operation(summary = "북마크 수(인기 순)로 탐방기 정보 4개를 가져오는 컨트롤러", description = "메인에서 탐방기 게시글을 이미지를 띄워줍니다. 만약 탐방기의 북마크 수가 0이라면 가장 최신의 탐방기 게시글을 가져옵니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 메인에 탐방기를 가져왔습니다."))
    public ResponseEntity<?> getExplorationByBookmark(){
        List<ExplorationDetailDto> explorationDetailDtoList = homeService.getExplorationByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDtoList, "메인 탐방기 정보 조회 성공"));
    }

    //메인에서 담소 게시글을 띄워줍니다.
    //북마크 수가 가장 높은 게시글 4개를 띄워줍니다.
    @GetMapping("/talk")
    @Operation(summary = "북마크 수(인기 순)로 담소 정보 4개를 가져오는 컨트롤러", description = "메인에서 담소 게시글을 띄워줍니다. 만약 담소의 북마크 수가 0이라면 가장 최신의 담소 게시글을 가져옵니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 메인에 탐방기를 가져왔습니다."))
    public ResponseEntity<?> getTalkByBookmark(){
        List<TalkDetailResponseDto> talkDetailDtoList = homeService.getTalkByBookmark();
        return ResponseEntity.ok(ApiResponseDto.success(talkDetailDtoList, "메인 담소 정보 조회 성공"));
    }
}
