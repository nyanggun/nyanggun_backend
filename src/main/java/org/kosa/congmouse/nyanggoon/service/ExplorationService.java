package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.ExplorationRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExplorationService {
    private final ExplorationRepository explorationRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Exploration createExploration(ExplorationCreateDto explorationCreateDto){
        Exploration exploration = Exploration
                .builder()
                .title(explorationCreateDto.getTitle())
                .content(explorationCreateDto.getContent())
                .relatedHeritage(explorationCreateDto.getRelatedHeritage())
                .member(memberRepository
                        .findById(explorationCreateDto.getMemberId())
                        .orElseThrow(()->new RuntimeException("회원이 존재하지 않습니다!")))
                .build();
        explorationRepository.save(exploration);
        return exploration;
    }

    public Exploration viewExploration(Long id) {
        Exploration exploration = explorationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException(("게시글이 존재하지 않습니다!")));
        return exploration;
    }

    @Transactional
    public void deleteExploration(Long id){
        explorationRepository.deleteById(id);
    }

    @Transactional
    public Exploration updateExploration(ExplorationUpdateDto explorationUpdateDto){
        Exploration exploration = explorationRepository.findById(explorationUpdateDto.getId()).orElseThrow(() -> {
            throw new RuntimeException("게시글이 존재하지 않습니다");
        });
        exploration.update(explorationUpdateDto);
        return exploration;
    }
}
