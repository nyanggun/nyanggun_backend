package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Security;
import java.util.List;

@RestController
@RequestMapping("/explorations")
@RequiredArgsConstructor
@Slf4j
public class ExplorationController {
    private final ExplorationService explorationService;

    @GetMapping("")
    public ResponseEntity getExplorationList(){
        List<ExplorationDetailDto> explorationList = explorationService.getExplorationList().reversed();
        return ResponseEntity.status(HttpStatus.OK).body(explorationList);
    }

    @PostMapping("")
    public ResponseEntity<?> postExploration(@RequestPart("dto") ExplorationCreateDto explorationCreateDto, @RequestPart(name = "images", required = false) List<MultipartFile> imageFileList) throws IOException {
        ExplorationDetailDto explorationDetailDto = explorationService.createExploration(explorationCreateDto, imageFileList);
        return ResponseEntity.ok(ApiResponseDto.success(explorationDetailDto, "문화재 탐방기 작성 완료"));
    }

    @GetMapping("/{id}")
    public ResponseEntity getExploration(@PathVariable Long id){
        ExplorationDetailDto explorationDetailDto = explorationService.viewExploration(id);
        return ResponseEntity.status(HttpStatus.OK).body(explorationDetailDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchExploration(@PathVariable Long id, @RequestPart("dto") ExplorationUpdateDto explorationUpdateDto, @RequestPart("images") List<MultipartFile> impageFileList, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        log.debug("글쓴이id={} 수정하는사람id={}", explorationUpdateDto.getMemberId(), memberDetails.getMember());
        Exploration exploration = explorationService.editExploration(explorationUpdateDto, memberDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(ExplorationDetailDto.from(exploration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteExploration(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        explorationService.deleteExploration(id, memberDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // 북마크 생성 요청
    @PostMapping("/bookmarks")
    public ResponseEntity<?> postExplorationBookmark(@RequestBody ExplorationBookmarkRequestDto explorationBookmarkRequestDto){
        log.debug("{} {}", explorationBookmarkRequestDto.getExplorationId(), explorationBookmarkRequestDto.getMemberId());
        explorationService.createExplorationBookmark(explorationBookmarkRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(201, "북마크 생성 완료"));
    }

    @DeleteMapping("/bookmarks")
    public ResponseEntity<?> deleteExplorationBookmark(@RequestBody ExplorationBookmarkRequestDto explorationBookmarkRequestDto) {
        log.debug("{} {}", explorationBookmarkRequestDto.getExplorationId(), explorationBookmarkRequestDto.getMemberId());
        explorationService.deleteExplorationBookmark(explorationBookmarkRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(204, "북마크 삭제 완료"));
    }

    // 북마크 체크 여부 조회
    @GetMapping("/bookmarks")
    public ResponseEntity<?> getExplorationBookmarkChecked(@RequestParam Long memberId, Long explorationId) {
        log.debug("{} {}", memberId, explorationId);
        Boolean result = explorationService.checkExplorationBookmarked(memberId, explorationId);
        return ResponseEntity.ok(ApiResponseDto.success(result, "북마크 여부 조회 완료"));
    }

}
