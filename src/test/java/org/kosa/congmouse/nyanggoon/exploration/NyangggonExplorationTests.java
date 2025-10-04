package org.kosa.congmouse.nyanggoon.exploration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.controller.ExplorationController;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class NyangggonExplorationTests {
    @Autowired
    ExplorationService explorationService;
    @Test
    void CreateTest(){
        //given
        ExplorationCreateDto explorationCreateDto = ExplorationCreateDto
                .builder()
                .title("test")
                .content("test content")
                .relatedHeritage("test heritage")
                .MemberId(1L)
                .build();
        //when
        ExplorationDetailDto explorationDetailDto = explorationService.createExploration(explorationCreateDto);

        //then
        assertThat(explorationDetailDto.getContent()).isEqualTo(explorationCreateDto.getContent());
    }
}
