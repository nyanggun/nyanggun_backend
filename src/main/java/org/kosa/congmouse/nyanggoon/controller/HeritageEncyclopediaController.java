package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.HeritageEncyclopediaService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    public ResponseEntity<?> getHeritageEncyclopediaNameList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size, @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        Page<HeritageEncyclopediaResponseDto> result = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByKoreanName(page, size, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(result, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 리스트-인기순
    @GetMapping("/list/popular")
    public ResponseEntity<?> getHeritageEncyclopediaPopularList(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "4") int size){
        Page<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtosPage = heritageEncyclopediaService.getAllHeritageEncyclopediasSortedByPopular(page, size);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtosPage, "문화재 도감 목록 조회 성공"));
    }

    // 문화재 도감 상세페이지
    @GetMapping("/detail/{HeritageEncyclopediaId}")
    public ResponseEntity<?> getHeritageEncyclopediaDetail(@PathVariable Long HeritageEncyclopediaId, @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        HeritageEncyclopediaResponseDto heritageEncyclopediaResponseDto = heritageEncyclopediaService.getHeritageEncyclopediaById(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDto, "문화재 조회 성공"));
    }

    // 북마크 저장
    @PostMapping("/bookmark/{HeritageEncyclopediaId}")
    public ResponseEntity<?> postBookmark(@PathVariable Long HeritageEncyclopediaId, @AuthenticationPrincipal CustomMemberDetails member){
        log.info("북마크 생성");
        Long memberId = (member != null) ? member.getMemberId() : null;
        EncyclopediaBookmarkDto bookmarkDto = heritageEncyclopediaService.saveBookmark(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(bookmarkDto, "북마크 등록 성공"));
    }

    // 북마크 삭제
    @DeleteMapping("/bookmark/{HeritageEncyclopediaId}")
    public ResponseEntity<?> deleteBookmark(@PathVariable Long HeritageEncyclopediaId, @AuthenticationPrincipal CustomMemberDetails member){
        log.info("북마크 삭제");
        Long memberId = (member != null) ? member.getMemberId() : null;
        EncyclopediaBookmarkDto bookmarkDto = heritageEncyclopediaService.deleteBookmark(HeritageEncyclopediaId, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(bookmarkDto, "북마크 삭제 성공"));
    }

    // 검색 기능
    @GetMapping("/search")
    public ResponseEntity<?> getHeritageEncyclopediaSearch(@RequestParam String keyword, @RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "4")int size, @AuthenticationPrincipal CustomMemberDetails member){
        log.info("검색 시작 키워드 {} ", keyword);
        Long memberId = (member != null) ? member.getMemberId() : null;
        Page<HeritageEncyclopediaResponseDto> heritageEncyclopediaResponseDtosPage = heritageEncyclopediaService.searchHeritageEncyclopedia(keyword, page, size, memberId);
        return ResponseEntity.ok(ApiResponseDto.success(heritageEncyclopediaResponseDtosPage, "문화재 검색 성공"));
    }
}
