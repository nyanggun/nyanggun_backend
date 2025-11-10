package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@Tag(name = "Google Map API 처리", description = "Google Map 좌표-주소 변환 API")
@RestController
@RequestMapping("/googlemap")
@Slf4j
public class GoogleMapController {

    @Value("${spring.google.map.key}")
    private String googleMapKey;

    @Operation(
            summary = "Google Map 좌표로 실제 주소 호출",
            description = "지도의 좌표를 이용해 실제 주소를 호출합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공")
    })
    @GetMapping("/coordinate")
    public ResponseEntity<?> getMapCoordinate(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng){
        String url = String.format("https://maps.googleapis.com/maps/api/geocode/json?latlng=%s,%s&key=%s&language=ko", lat, lng, googleMapKey
        );

        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);

        try{
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);
            String address = "주소를 찾을 수 없습니다.";

            if (root.has("results") && root.get("results").isArray() && root.get("results").size() > 0) {
                address = root.get("results").get(0).get("formatted_address").asText();
            }
            return ResponseEntity.ok(Map.of("address", address));
        }catch (Exception e) {
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }
}
