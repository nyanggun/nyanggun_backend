package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.kosa.congmouse.nyanggoon.service.HeritageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/heritages")
@RequiredArgsConstructor
public class HeritageController {

    private final HeritageService heritageService;

    // 문화재 db 저장
    @PostMapping("/save")
    public ResponseEntity<?> postHeritage(){
        heritageService.saveHeritageList();
        return ResponseEntity.ok("문화재 정보 저장 완료");
    }
}
