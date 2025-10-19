package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Slf4j
public class BadgeController {

    public final BadgeService badgeService;

    // 지도에 표시할 모든 증표
    @GetMapping("/markers")
    public ResponseEntity<?> getHeritageList(){
        List<HunterBadgeMarkerResponseDto> dtoList = badgeService.getHunterBadgeList();
        return ResponseEntity.ok(ApiResponseDto.success(dtoList, "지도에 문화재 표시 완료"));
    }

    // 증표 획득
    @PostMapping("/acquire/{badgeId}")
    public ResponseEntity<?> postBadgeAquireByBadgeId(@PathVariable Long badgeId, @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        HunterBadgeAcquireResponseDto hunterBadgeAcquireResponseDto = badgeService.saveAquiredBadge(badgeId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(hunterBadgeAcquireResponseDto, "증표 획득 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @GetMapping("/acquired")
    public ResponseEntity<?> getAcquiredBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<Long> acquiredBadgeIds  = badgeService.findAcquiredBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(acquiredBadgeIds, "획득한 증표 id 목록 조회 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @GetMapping("/badgebox")
    public ResponseEntity<?> getCollectedBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        log.info("대체 왜?");
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<HunterBadgesAcquisitionResponseDto> HunterBadgesAcquisitionResponseDto  = badgeService.findCollectedBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(HunterBadgesAcquisitionResponseDto, "획득한 증표 id 목록 조회 성공"));
    }
}
