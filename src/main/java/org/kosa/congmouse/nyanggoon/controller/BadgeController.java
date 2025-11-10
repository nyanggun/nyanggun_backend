package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.exception.GlobalErrorResponse;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "문화재 증표", description = "문화재 증표 관련 API")
@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Slf4j
public class BadgeController {

    public final BadgeService badgeService;

    // 지도에 표시할 모든 증표
    @Operation(
            summary = "증표 리스트 요청",
            description = "지도에 표시할 모든 사냥꾼 증표 리스트를 가져옵니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "모든 증표 리스트 요청 성공",
                    content = @Content(schema = @Schema(implementation = ApiResponseDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "사용자가 요청한 리소스를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류가 발생했습니다.",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class))
            )
    })
    @GetMapping("/markers")
    public ResponseEntity<ApiResponseDto<?>> getHeritageList(){
        List<HunterBadgeMarkerResponseDto> dtoList = badgeService.getHunterBadgeList();
        log.info("증표리스트 {}", dtoList);
        return ResponseEntity.ok(ApiResponseDto.success(dtoList, "지도에 문화재 표시 완료"));
    }

    // 증표 획득
    @PostMapping("/acquire/{badgeId}")
    public ResponseEntity<ApiResponseDto<?>> postBadgeAquireByBadgeId(@PathVariable Long badgeId,
                                                                    @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        HunterBadgeAcquireResponseDto hunterBadgeAcquireResponseDto = badgeService.saveAquiredBadge(badgeId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(hunterBadgeAcquireResponseDto, "증표 획득 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @GetMapping("/acquired")
    public ResponseEntity<ApiResponseDto<?>> getAcquiredBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<Long> acquiredBadgeIds  = badgeService.findAcquiredBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(acquiredBadgeIds, "획득한 증표 id 목록 조회 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @GetMapping("/badgebox")
    public ResponseEntity<ApiResponseDto<?>> getCollectedBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        log.info("대체 왜?");
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<HunterBadgesAcquisitionResponseDto> HunterBadgesAcquisitionResponseDto  = badgeService.findCollectedBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(HunterBadgesAcquisitionResponseDto, "획득한 증표 id 목록 조회 성공"));
    }
}
