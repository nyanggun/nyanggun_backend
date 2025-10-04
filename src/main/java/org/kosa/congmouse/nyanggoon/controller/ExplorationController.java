package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exploration")
@RequiredArgsConstructor
public class ExplorationController {
    private final ExplorationService explorationService;

    @PostMapping("")
    public ResponseEntity postExploration(@RequestBody ExplorationCreateDto explorationCreateDto){
        Exploration exploration = explorationService.createExploration(explorationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExplorationDetailDto.from(exploration));
    }

    @GetMapping("/{id}")
    public ResponseEntity getExploration(@PathVariable Long id){
        Exploration exploration = explorationService.viewExploration(id);
        return ResponseEntity.status(HttpStatus.OK).body(ExplorationDetailDto.from(exploration));
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchExploration(@PathVariable Long id, @RequestBody ExplorationUpdateDto explorationUpdateDto){
        Exploration exploration = explorationService.updateExploration(explorationUpdateDto);
        return ResponseEntity.status(HttpStatus.OK).body(ExplorationDetailDto.from(exploration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteExploration(@PathVariable Long id){
        explorationService.deleteExploration(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
