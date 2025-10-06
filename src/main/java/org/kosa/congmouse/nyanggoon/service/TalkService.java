package org.kosa.congmouse.nyanggoon.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.TalkDetailResponseDto;
import org.kosa.congmouse.nyanggoon.dto.TalkListSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.repository.TalkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

//담소 게시글의 Service 입니다.
@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
public class TalkService {

    private final TalkRepository talkRepository;

    //해당하는 담소 게시글을 찾는 메소드 입니다.
    public Talk findTalkById(Long id){
        return talkRepository.findById(id).orElseThrow(()-> new IllegalArgumentException("담소 id"+id+"에 해당하는 게시글을 찾을 수 없습니다."));
    }

    //모든 담소 게시글을 불러오는 메소드 입니다.
    public List<TalkListSummaryResponseDto> findAllTalkList() {
        log.info("담소 게시물을 전부 출력합니다.");
        List<Talk> talkList = talkRepository.findAllWithMember();
        return talkList.stream().map(TalkListSummaryResponseDto::from).collect(Collectors.toUnmodifiableList());
    }

    //담소 게시글의 상세를 조회하는 메소드 입니다.
    public TalkDetailResponseDto findTalkDetail(Long id) {
        log.info("해당 담소 게시물의 내용을 확인합니다. {}", id);
        TalkDetailResponseDto talk = talkRepository.findTalkDetail(id);
        return talk;
    }

    //담소 게시글을 삭제하는 메소드 입니다.
    @Transactional
    public void deleteTalk(Long id){
        log.info("해당하는 담소 게시물을 삭제합니다. {}", id);
        findTalkById(id);
        //자동 지원되는 메소드를 사용한다.
        talkRepository.deleteById(id);
    }


}
