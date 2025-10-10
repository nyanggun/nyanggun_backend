package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDeleteDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.security.user.CustomMemberDetails;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Security;
import java.util.List;

@RestController
@RequestMapping("/explorations")
@RequiredArgsConstructor
@Slf4j
public class ExplorationController {
    private final ExplorationService explorationService;

    @GetMapping("")
    public ResponseEntity getExplorationList(){
        List<ExplorationDetailDto> explorationList = explorationService.getExplorationList().reversed();
        return ResponseEntity.status(HttpStatus.OK).body(explorationList);
    }

    @PostMapping("")
    public ResponseEntity postExploration(@RequestBody ExplorationCreateDto explorationCreateDto){
        Exploration exploration = explorationService.createExploration(explorationCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ExplorationDetailDto.from(exploration));
    }

    @GetMapping("/{id}")
    public ResponseEntity getExploration(@PathVariable Long id){
        ExplorationDetailDto explorationDetailDto = explorationService.viewExploration(id);
        return ResponseEntity.status(HttpStatus.OK).body(explorationDetailDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity patchExploration(@PathVariable Long id, @RequestBody ExplorationUpdateDto explorationUpdateDto, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        log.debug("글쓴이id={} 수정하는사람id={}", explorationUpdateDto.getMemberId(), memberDetails.getMember());
        Exploration exploration = explorationService.editExploration(explorationUpdateDto, memberDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.OK).body(ExplorationDetailDto.from(exploration));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity deleteExploration(@PathVariable Long id, @AuthenticationPrincipal CustomMemberDetails memberDetails){
        explorationService.deleteExploration(id, memberDetails.getMemberId());
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
}
