package org.kosa.congmouse.nyanggoon.exploration;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.controller.ExplorationController;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.service.ExplorationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class NyangggonExplorationTests {
    private static final Logger log = LoggerFactory.getLogger(NyangggonExplorationTests.class);
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
                .memberId(1L)
                .build();
        //when
        Exploration exploration = explorationService.createExploration(explorationCreateDto);

        //then
        assertThat(exploration.getContent()).isEqualTo(explorationCreateDto.getContent());
    }

    @Test
    void readTest(){
        //given
        Long id = 1L;

        //when
        Exploration exploration = explorationService.viewExploration(id);

        //then
        assertThat(exploration).isNotNull();
        log.debug(exploration.toString());
    }

    @Test
    void updateTest(){
        // given
        ExplorationUpdateDto explorationUpdateDto
                = new ExplorationUpdateDto(1L, "update test title", "update test content", "update realated heritage", 1L);

        // when
        explorationService.updateExploration(explorationUpdateDto);

        //then
        Exploration exploration = explorationService.viewExploration(1L);
        ExplorationDetailDto explorationDetailDto = ExplorationDetailDto.from(exploration);
        assertThat(explorationDetailDto.getTitle()).isEqualTo(explorationUpdateDto.getTitle());
    }

//    @Test
//    void deleteTest(){
//        //given
//        Long id = 1L;
//
//        //when
//        explorationService.deleteExploration(id);
//
//        //then
//        assertThatThrownBy(()->{
//            explorationService.viewExploration(id);
//        })
//                .isInstanceOf(RuntimeException.class)
//                .withFailMessage("게시글이 존재하지 않습니다");
//    }
}
