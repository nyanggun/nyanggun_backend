package org.kosa.congmouse.nyanggoon.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ApiResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/googlemap")
@Slf4j
public class GoogleMapController {

    @Value("${spring.google.map.key}")
    private String googleMapKey;

    @GetMapping("/coordinate")
    public ResponseEntity<?> getMapCoordinate(@RequestParam BigDecimal lat, @RequestParam BigDecimal lng){
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
