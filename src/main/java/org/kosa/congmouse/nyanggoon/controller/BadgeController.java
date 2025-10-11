package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HunterBadgeAcquireResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HeritageListResponseDto;
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
    public ResponseEntity<?> getHeritageListTemp(){
        String url = "https://www.khs.go.kr/cha/SearchKindOpenapiList.do?pageUnit=1000&ccbaCncl=N&ccbaKdcd=11&ccbaCtcd=11";
        RestTemplate restTemplate = new RestTemplate();
        try{
            String xmlResponse = restTemplate.getForObject(url, String.class);
            XmlMapper xmlMapper = new XmlMapper();
            Map<String, Object> root = xmlMapper.readValue(xmlResponse, Map.class);
            List<Map<String, Object>> itemList = (List<Map<String, Object>>) root.get("item");
            String itemListToJson = xmlMapper.writeValueAsString(itemList);
            List<HeritageListResponseDto> heritageListResponseDtos =
                    xmlMapper.readValue(itemListToJson, new TypeReference<List<HeritageListResponseDto>>() {});
//            heritageListResponseDtos.forEach(dto ->log.info(dto.getName()));
            // 각 DTO에 badgeUrl 주입
            heritageListResponseDtos.forEach(dto -> {
                try {
                    // CDN 주소 생성
                    String badgeUrl =
                            "https://cdn.jsdelivr.net/gh/nyanggun/nyanggoon-badges@main/" + dto.getName() + ".png";

                    dto.setBadgeUrl(badgeUrl);
                } catch (Exception e) {
                    // 혹시 이름이 null이거나 인코딩 에러일 경우 기본 이미지로 대체
                    dto.setBadgeUrl("https://cdn.jsdelivr.net/gh/nyanggun/nyanggoon-badges@main/기본.png");
                }
            });
            return ResponseEntity.ok(ApiResponseDto.success(heritageListResponseDtos, "유적 조회 성공"));
        }catch(Exception e){
            return ResponseEntity.internalServerError().body(ApiResponseDto.error("500", "서버 오류 발생"));
        }
    }

    @PostMapping("/acquire/{id}")
    public ResponseEntity<?> postBadgeAquireByBadgeId(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails user){
        HunterBadgeAcquireResponseDto hunterBadgeAcquireResponseDto = badgeService.badgeAquire(id, user.getMemberId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(hunterBadgeAcquireResponseDto, "증표 획득 성공!"));
    }
}
