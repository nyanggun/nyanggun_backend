package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exploration")
@RequiredArgsConstructor
public class ExplorationController {
    @Autowired
    private final ExplorationService explorationService;

    @PostMapping("/")
    public ResponseEntity postExploration(@RequestBody ExplorationCreateDto explorationCreateDto){
        ExplorationDetailDto explorationDetailDto = explorationService.createExploration(explorationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(explorationDetailDto);
    }
}
