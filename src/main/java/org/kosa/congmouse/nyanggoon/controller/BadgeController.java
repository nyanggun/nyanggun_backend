package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

@Tag(name = "사냥꾼 증표", description = "사냥꾼 증표 관련 API")
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
                    description = "지도에 문화재 표시 완료",
                    content = @Content(
                            schema = @Schema(implementation = HunterBadgeMarkerResponseDto.class)
                    )),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "리소스 없음",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = GlobalErrorResponse.class)))
    })
    @GetMapping("/markers")
    public ResponseEntity<ApiResponseDto<List<HunterBadgeMarkerResponseDto>>> getHeritageList(){
        List<HunterBadgeMarkerResponseDto> dtoList = badgeService.getHunterBadgeList();
        log.info("증표리스트 {}", dtoList);
        return ResponseEntity.ok(ApiResponseDto.success(dtoList, "지도에 문화재 표시 완료"));
    }

    // 증표 획득
    @Operation(
            summary = "증표 획득 요청",
            description = "획득한 증표를 저장합니다.",
            security = {@SecurityRequirement(name = "JWT Token")}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HunterBadgeAcquireResponseDto.class)
                    )
            )
    })
    @PostMapping("/acquire/{badgeId}")
    public ResponseEntity<ApiResponseDto<HunterBadgeAcquireResponseDto>> postBadgeAquireByBadgeId(
            @Parameter(description = "증표 ID", example = "107", required = true)
            @PathVariable Long badgeId,
            @AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        HunterBadgeAcquireResponseDto hunterBadgeAcquireResponseDto = badgeService.saveAquiredBadge(badgeId, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(hunterBadgeAcquireResponseDto, "증표 획득 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @Operation(
            summary = "멤버의 획득한 증표 ID 리스트 요청",
            description = "지도에 증표 획득 여부를 표시하기 위해 멤버가 획득한 증표를 가져옵니다.",
            security = {@SecurityRequirement(name = "JWT Token")})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "획득한 증표 id 목록 조회 성공",
                    content = @Content(
                            schema = @Schema(implementation = List.class),
                            examples = @ExampleObject(value = """
                    {
                      "success": true,
                      "data": {
                        "acquiredBadgeIds": [1, 2, 3, 10]
                      },
                      "message": "획득한 증표 id 목록 조회 성공"
                    }
                    """)))
    })
    @GetMapping("/acquired")
    public ResponseEntity<ApiResponseDto<List<Long>>> getAcquiredBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<Long> acquiredBadgeIds  = badgeService.findAcquiredBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(acquiredBadgeIds, "획득한 증표 id 목록 조회 성공"));
    }

    // 지도에 표시할 이미 획득한 증표(유저에 따라)
    @Operation(
            summary = "멤버의 획득한 증표 리스트 요청(증표함)",
            description = "증표함에 증표 획득 여부를 표시하기 위해 멤버가 획득한 증표를 가져옵니다.",
            security = {@SecurityRequirement(name = "JWT Token")}
    )
    @ApiResponse(
            responseCode = "200",
            description = "성공",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(type = "array", implementation = HunterBadgesAcquisitionResponseDto.class)
            )
    )
    @GetMapping("/badgebox")
    public ResponseEntity<ApiResponseDto<List<HunterBadgesAcquisitionResponseDto>>> getCollectedBadgesByMemberId(@AuthenticationPrincipal CustomMemberDetails member){
        log.info("대체 왜?");
        Long memberId = (member != null) ? member.getMemberId() : null;
        List<HunterBadgesAcquisitionResponseDto> HunterBadgesAcquisitionResponseDto  = badgeService.findCollectedBadgesList(memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(HunterBadgesAcquisitionResponseDto, "획득한 증표 id 목록 조회 성공"));
    }
}
