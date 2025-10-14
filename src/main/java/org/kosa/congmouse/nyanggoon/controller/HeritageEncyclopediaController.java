package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.service.HeritageEncyclopediaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/heritages")
@RequiredArgsConstructor
@Slf4j
public class HeritageEncyclopediaController {

    private final HeritageEncyclopediaService heritageEncyclopediaService;

    // 문화재 db 저장
    @PostMapping("/save")
    public ResponseEntity<?> postHeritage(){
        heritageEncyclopediaService.saveHeritageList();
        return ResponseEntity.ok("문화재 정보 저장 완료");
    }

    // 문화재 도감 리스트-가나다순
    @GetMapping("/list/name")
    public ResponseEntity<?> getHeritageEncyclopediaNameList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size){
        Page<HeritageEncyclopediaResponseDto> result = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByKoreanName(page, size);
        return ResponseEntity.ok(ApiResponseDto.success(result, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 리스트-인기순
    @GetMapping("/list/popular")
    public ResponseEntity<?> getHeritageEncyclopediaPopularList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size){
        Page<HeritageEncyclopediaResponseDto> result = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByPopular(page, size);
        return ResponseEntity.ok(ApiResponseDto.success(result, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 상세페이지
    @GetMapping("/detail/{HeritageEncyclopediaId}")
    public ResponseEntity<?> getHeritageEncyclopediaDetail(@PathVariable Long HeritageEncyclopediaId){
        HeritageEncyclopediaResponseDto heritageEncyclopediaResponseDto = heritageEncyclopediaService.getHeritageEncyclopediaById(HeritageEncyclopediaId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDto, "문화재 조회 성공"));
    }
}
