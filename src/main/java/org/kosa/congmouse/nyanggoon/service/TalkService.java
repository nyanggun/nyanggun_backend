package org.kosa.congmouse.nyanggoon.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkBookmark;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkBookmarkRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkCommentRepository;
import org.kosa.congmouse.nyanggoon.repository.TalkRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final TalkBookmarkRepository talkBookmarkRepository;

    //모든 담소 게시글을 불러오는 메소드 입니다.
    public List<TalkListSummaryResponseDto> findAllTalkList(String username) {

        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용

        log.info("담소 게시물을 전부 출력합니다.");
        List<Talk> talks = talkRepository.findAll(); // 게시글 목록
        List<Object[]> commentCounts = talkRepository.countCommentsPerTalk(); //댓글 개수
        List<Object[]> bookmarkCounts = talkRepository.countBookmarksPerTalk(); //북마크 개수

        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // talkId
                        arr -> (Long) arr[1]   // count
                ));

        Map<Long, Long> bookmarkCountMap = bookmarkCounts.stream()
                .collect(Collectors.toMap(
                        arrBook -> (Long) arrBook[0],  // talkId
                        arrBook -> (Long) arrBook[1]   // count
                ));

        //북마크 여부 가져오기
        Set<Long> bookmarkedTalkIdSet = new HashSet<>();
        if (member != null) { // 로그인 사용자만 체크
            List<Long> bookmarkedTalkIds = talkBookmarkRepository.findTalkIdsByMember(member);
            bookmarkedTalkIdSet.addAll(bookmarkedTalkIds);
        }


        List<TalkListSummaryResponseDto> talkListSummaryResponseDto = talks.stream()
                .map(t -> TalkListSummaryResponseDto.builder()
                        .talkId(t.getId())
                        .title(t.getTitle())
                        .content(t.getContent())
                        .memberId(t.getMember().getId())
                        .nickname(t.getMember().getNickname())
                        .createdAt(t.getCreatedAt())
                        .commentCount(commentCountMap.getOrDefault(t.getId(), 0L))
                        .bookmarkCount(bookmarkCountMap.getOrDefault(t.getId(), 0L)) // 북마크도 동일하게 처리
                        .isBookmarked(bookmarkedTalkIdSet.contains(t.getId())) //유저의 북마크 여부
                        .build())
                .collect(Collectors.toList());

        return talkListSummaryResponseDto;
    }

    //담소 게시글의 상세를 조회하는 메소드 입니다.
    //댓글도 함께 가져옵니다.
    public TalkDetailResponseDto findTalkDetail(Long id, String username) {
        log.info("해당 담소 게시물의 내용을 확인합니다. {}", id);
        // 게시글 엔티티 가져오기
        Talk talk = talkRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        //댓글, 북마크 개수 가져오기
        long commentCount = talkRepository.countCommentsByTalkId(id);
        long bookmarkCount = talkRepository.countBookmarksByTalkId(id);

        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용
        //북마크여부 가져오기
        boolean isBookmarked = false;
        if (member != null) {
            // 해당 게시글만 확인
            isBookmarked = talkBookmarkRepository.getBookmarkByMemberAndTalk(member.getId(), talk.getId()) != null;

        }


        //해당 게시글의 댓글들을 가져옵니다.
        List<TalkComment> talkComment = talkCommentRepository.findTalkComment(id);
        // DTO로 변환합니다.
        List<TalkCommentResponseDto> talkCommentDtos = talkComment.stream()
                .map(TalkCommentResponseDto::from)
                .toList();

        return TalkDetailResponseDto.from(talk, talkCommentDtos, commentCount, bookmarkCount, isBookmarked);
    }

    //담소 게시글을 작성하는 메소드 입니다.
    @Transactional
    public Long createTalk(TalkCreateRequestDto talkCreateRequestDto) {
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

        return savedTalk.getId();

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
    public TalkComment createTalkComment(TalkCommentCreateRequestDto talkCommentCreateRequestDto) {
        log.info("담소 댓글 작성 시작");

        //해당 회원이 있는 지 확인한다.
        Member member = memberRepository.findById(talkCommentCreateRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        //해당 게시글이 있는 지 확인한다.
        Talk talk = talkRepository.findById(talkCommentCreateRequestDto.getTalkId()).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //대댓글이라면 부모 댓글을 조회한다.
        TalkComment parentComment = null;
        // 부모 댓글이 존재할 경우
        if (talkCommentCreateRequestDto.getParentCommentId() != null) {
            //현재클릭한 댓글
            TalkComment tempParent = talkCommentRepository.findById(talkCommentCreateRequestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

            // 만약 지금 작성하려는 댓글의 부모가 '대댓글'이라면, (즉 대대댓글을 작성하려면)
            // 그 대댓글의 부모(즉, 최상위 댓글)를 부모로 설정
            // 즉 대대댓글이 아니라 해당 최상위 댓글의 대댓글로 들어가게됨
            if (tempParent.getParentComment() != null) {
                //지금 내가 생성하려는 댓글의 부모를 같은 부모로 둬라
                parentComment = tempParent.getParentComment();
            } else {
                //아니면 그 댓글이 부모가 된다. (일반적인 대댓글)
                parentComment = tempParent;
            }
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

        return savedTalkComment;
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

    //게시글을 북마크하는 메소드 입니다.
    @Transactional
    public void createTalkBookmark(Long talkId, String username) {
        // 사용자 조회
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        // 게시글 조회
        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //북마크 객체 생성
        TalkBookmark talkBookmark = TalkBookmark.builder()
                .member(member)   // 현재 로그인한 사용자 엔티티
                .talk(talk)       // 북마크할 게시글 엔티티
                .build();
        //북마크 저장
        talkBookmarkRepository.save(talkBookmark);

    }

    //게시글 북마크를 취소하는 메소드 입니다.
    @Transactional
    public void deleteTalkBookmark(Long talkId, String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

      Long bookmarkId = talkBookmarkRepository.getBookmarkWithTalkId(talkId);

       talkBookmarkRepository.deleteById(bookmarkId);

    }

    //게시글을 검색하는 메소드 입니다.
    public List<TalkListSummaryResponseDto> findTalkListWithKeyword(String username, String keyword) {
        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용

        log.info("담소 게시물 검색 결과를 출력합니다.");
        List<Talk> talks = talkRepository.findTalkListWithKeyword(keyword); // 게시글 목록
        List<Object[]> commentCounts = talkRepository.countCommentsPerTalk(); //댓글 개수
        List<Object[]> bookmarkCounts = talkRepository.countBookmarksPerTalk(); //북마크 개수

        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // talkId
                        arr -> (Long) arr[1]   // count
                ));

        Map<Long, Long> bookmarkCountMap = bookmarkCounts.stream()
                .collect(Collectors.toMap(
                        arrBook -> (Long) arrBook[0],  // talkId
                        arrBook -> (Long) arrBook[1]   // count
                ));

        //북마크 여부 가져오기
        Set<Long> bookmarkedTalkIdSet = new HashSet<>();
        if (member != null) { // 로그인 사용자만 체크
            List<Long> bookmarkedTalkIds = talkBookmarkRepository.findTalkIdsByMember(member);
            bookmarkedTalkIdSet.addAll(bookmarkedTalkIds);
        }


        List<TalkListSummaryResponseDto> talkListSummaryResponseDto = talks.stream()
                .map(t -> TalkListSummaryResponseDto.builder()
                        .talkId(t.getId())
                        .title(t.getTitle())
                        .content(t.getContent())
                        .memberId(t.getMember().getId())
                        .nickname(t.getMember().getNickname())
                        .createdAt(t.getCreatedAt())
                        .commentCount(commentCountMap.getOrDefault(t.getId(), 0L))
                        .bookmarkCount(bookmarkCountMap.getOrDefault(t.getId(), 0L)) // 북마크도 동일하게 처리
                        .isBookmarked(bookmarkedTalkIdSet.contains(t.getId())) //유저의 북마크 여부
                        .build())
                .collect(Collectors.toList());

        return talkListSummaryResponseDto;
    }
}
