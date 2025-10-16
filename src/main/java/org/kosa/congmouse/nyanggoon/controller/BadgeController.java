package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.BadgeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/badges")
@RequiredArgsConstructor
@Slf4j
public class BadgeController {

    public final BadgeService badgeService;

    @GetMapping("/markers")
    public ResponseEntity<?> getHeritageList(){
        List<HeritageEncyclopediaBadgeResponseDto> dtoList = badgeService.getHeritageEncyclopediaList();
        return ResponseEntity.ok(ApiResponseDto.success(dtoList, "지도에 문화재 표시 완료"));
    }

    @PostMapping("/acquire/{heritageId}")
    public ResponseEntity<?> postBadgeAquireByBadgeId(@PathVariable Long heritageId, @AuthenticationPrincipal CustomMemberDetails user){
        HunterBadgeAcquireResponseDto hunterBadgeAcquireResponseDto = badgeService.badgeAquire(heritageId, user.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(hunterBadgeAcquireResponseDto, "증표 획득 성공!"));
    }
}
