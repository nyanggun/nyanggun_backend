    package org.kosa.congmouse.nyanggoon.repository;

    import org.kosa.congmouse.nyanggoon.dto.TalkCommentResponseDto;
    import org.kosa.congmouse.nyanggoon.dto.TalkDetailResponseDto;
    import org.kosa.congmouse.nyanggoon.entity.Talk;
    import org.kosa.congmouse.nyanggoon.entity.TalkComment;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;
    import java.util.Optional;

    @Repository
    public interface TalkRepository extends JpaRepository<Talk, Long> {

        //쿼리문은 엔티티 클래스명으로 작성해야 한다.
        @Query("SELECT t FROM Talk t JOIN FETCH t.member")
        List<Talk> findAllWithMember();

        @Query("SELECT t FROM Talk t JOIN FETCH t.member WHERE t.id = :id")
        TalkDetailResponseDto findTalkDetail(@Param("id") Long id);

        // 게시글별 댓글 수 조회
        @Query("SELECT t.id, COUNT(c) " +
                "FROM Talk t LEFT JOIN TalkComment c ON c.talk.id = t.id " +
                "GROUP BY t.id")
        List<Object[]> countCommentsPerTalk();

        // 게시글별 북마크 수 조회
        @Query("SELECT t.id, COUNT(b) " +
                "FROM Talk t LEFT JOIN TalkBookmark b ON b.talk.id = t.id " +
                "GROUP BY t.id")
        List<Object[]> countBookmarksPerTalk();

        // 댓글 개수
        @Query("SELECT COUNT(c) FROM TalkComment c WHERE c.talk.id = :talkId")
        long countCommentsByTalkId(@Param("talkId") Long talkId);

        // 북마크 개수
        @Query("SELECT COUNT(b) FROM TalkBookmark b WHERE b.talk.id = :talkId")
        long countBookmarksByTalkId(@Param("talkId") Long talkId);

        //검색 결과
        @Query("SELECT t FROM Talk t WHERE  t.title LIKE CONCAT('%', :keyword, '%') OR t.content  LIKE CONCAT('%', :keyword, '%')")
        List<Talk> findTalkListWithKeyword(String keyword);

        //findById(Long id) : 자동 지원
        // deleteById(Long id) : 자동 지원
    }
