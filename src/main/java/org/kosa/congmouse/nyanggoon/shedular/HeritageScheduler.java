package org.kosa.congmouse.nyanggoon.shedular;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.service.HeritageEncyclopediaService;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HeritageScheduler {

    private final HeritageEncyclopediaService heritageEncyclopediaService;

    // 스케줄러 자동 문화재 db 저장
    @Scheduled(cron = "0 0 3 1 1 *") // 1월 1일 새벽 3시
    @Scheduled(cron = "0 0 3 1 7 *") // 7월 1일 새벽 3시
    public void saveHeritageAuto(){
        heritageEncyclopediaService.saveHeritageList();
    }
}
