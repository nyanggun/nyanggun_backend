package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.ExplorationCreateDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.ExplorationBookmarkRepository;
import org.kosa.congmouse.nyanggoon.repository.ExplorationCommentRepository;
import org.kosa.congmouse.nyanggoon.repository.ExplorationRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ExplorationService {
    private final ExplorationRepository explorationRepository;
    private final MemberRepository memberRepository;
    private final ExplorationBookmarkRepository explorationBookmarkRepository;
    private final ExplorationCommentRepository explorationCommentRepository;

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
        ExplorationDetailDto explorationDetailDto = ExplorationDetailDto.from(exploration);
        explorationDetailDto.setBookmarkCount(explorationBookmarkRepository.countByExplorationId(id));
        explorationDetailDto.setCommentCount(explorationCommentRepository.countByExplorationId(id));
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

    public List<ExplorationDetailDto> getExplorationList() {
        List<Exploration> explorationList = explorationRepository.findAll();
        List<ExplorationDetailDto> explorationDetailDtoList = explorationList.stream()
                .map(ExplorationDetailDto::from)
                .peek(explorationDetailDto -> explorationDetailDto.setBookmarkCount(explorationBookmarkRepository.countByExplorationId(explorationDetailDto.getId())))
                .peek(explorationDetailDto -> explorationDetailDto.setCommentCount(explorationCommentRepository.countByExplorationId(explorationDetailDto.getId())))
                .collect(Collectors.toList());
        log.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        log.debug("{}", explorationDetailDtoList.get(0));
        return explorationDetailDtoList;
    }
}
