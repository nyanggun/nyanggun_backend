package org.kosa.congmouse.nyanggoon.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkCommentRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
//담소 게시글의 Service 입니다.
public class TalkService {

    private final TalkRepository talkRepository;
    private final MemberRepository memberRepository;
    private final TalkCommentRepository talkCommentRepository;

    //모든 담소 게시글을 불러오는 메소드 입니다.
    public List<TalkListSummaryResponseDto> findAllTalkList() {
        log.info("담소 게시물을 전부 출력합니다.");
        List<Talk> talkList = talkRepository.findAllWithMember();
        return talkList.stream().map(TalkListSummaryResponseDto::from).collect(Collectors.toUnmodifiableList());
    }

    //담소 게시글의 상세를 조회하는 메소드 입니다.
    //댓글도 함께 가져옵니다.
    public TalkDetailResponseDto findTalkDetail(Long id) {
        log.info("해당 담소 게시물의 내용을 확인합니다. {}", id);
        // 게시글 엔티티 가져오기
        Talk talk = talkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        //해당 게시글의 댓글들을 가져옵니다.
        List<TalkComment> talkComment = talkCommentRepository.findTalkComment(id);
        // DTO로 변환합니다.
        List<TalkCommentResponseDto> talkCommentDtos = talkComment.stream()
                .map(TalkCommentResponseDto::from)
                .toList();

        return TalkDetailResponseDto.from(talk, talkCommentDtos);
    }

    //담소 게시글을 작성하는 메소드 입니다.
    @Transactional
    public void createTalk(TalkCreateRequestDto talkCreateRequestDto) {
        log.info("담소 게시글 작성 시작");

        //해당 회원이 있는 지 확인한다.
        Member member = memberRepository.findById(talkCreateRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        log.info("회원 확인 완료. 게시글 객체를 생성합니다.");

        //담소 게시글 객체를 생성한다.
        Talk talk = Talk.builder()
                .title(talkCreateRequestDto.getTitle())
                .content(talkCreateRequestDto.getContent())
                .member(member)
                .build();

        //담소 게시글을 저장한다.
        Talk savedTalk = talkRepository.save(talk);
        log.info("담소 게시글 작성 완료: id={}, title={}", savedTalk.getId(), savedTalk.getTitle());

    }


    //담소 게시글을 수정하는 메소드 입니다.
    @Transactional
    public void updateTalk(Long talkId, TalkUpdateRequestDto talkUpdateRequestDto) {
        log.info("담소 게시글 수정 시작");
        Talk talk = talkRepository.findById(talkId).orElseThrow(()->new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));
        talk.update(talkUpdateRequestDto.getTitle(),  talkUpdateRequestDto.getContent());
    }

    //담소 게시글을 삭제하는 메소드 입니다.
    @Transactional
    public void deleteTalk(Long talkId){
        log.info("해당하는 담소 게시물을 삭제합니다. {}", talkId);
        talkRepository.findById(talkId);
        //자동 지원되는 메소드를 사용한다.
        talkRepository.deleteById(talkId);
    }

    //댓글을 작성하는 메소드 입니다.
    @Transactional
    public void createTalkComment(TalkCommentCreateRequestDto talkCommentCreateRequestDto) {
        log.info("담소 댓글 작성 시작");

        //해당 회원이 있는 지 확인한다.
        Member member = memberRepository.findById(talkCommentCreateRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        //해당 게시글이 있는 지 확인한다.
        Talk talk = talkRepository.findById(talkCommentCreateRequestDto.getTalkId()).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //대댓글이라면 부모 댓글을 조회한다.
        TalkComment parentComment = null;
        if (talkCommentCreateRequestDto.getParentCommentId() != null) {
            parentComment = talkCommentRepository.findById(talkCommentCreateRequestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));
        }

        log.info("회원 및 게시글 확인 완료. 댓글 객체를 생성합니다.");

        //담소 게시글 객체를 생성한다.
        TalkComment talkComment = TalkComment.builder()
                .content(talkCommentCreateRequestDto.getContent())
                .member(member)
                .talk(talk)
                .parentComment(parentComment) // null이면 일반 댓글
                .build();


        //담소 게시글을 저장한다.
        TalkComment savedTalkComment = talkCommentRepository.save(talkComment);
        log.info("담소 게시글 작성 완료: id={}, title={}", savedTalkComment.getId(), savedTalkComment.getContent());
    }


    //댓글을 수정하는 메소드 입니다.
    @Transactional
    public void updateTalkComment(Long commentId, TalkCommentUpdateRequestDto talkCommentUpdateRequestDto) {
        log.info("담소 댓글 수정 시작");

        TalkComment talkComment= talkCommentRepository.findById(commentId).orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        talkComment.update(talkCommentUpdateRequestDto.getContent());
    }

    //댓글을 삭제하는 메소드 입니다.
    @Transactional
    public void deleteTalkComemnt(Long commentId) {
        log.info("댓글 삭제 시작");
        talkCommentRepository.findById(commentId);
        //자동 지원되는 메소드를 사용한다.
        talkCommentRepository.deleteById(commentId);
        log.info("댓글 삭제 완료.");
    }


}
