package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<TalkComment, Long> {

    // 첫 페이지 조회
    @Query(value = """
    SELECT * FROM (
        SELECT 
            ec.id AS comment_id,
            ec.content AS content,
            ec.created_at AS created_at,
            ec.member_id AS member_id,
            m.nickname AS nickname,
            ec.exploration_id AS post_id,
            'EXPLORATION' AS category
        FROM exploration_comments ec
        JOIN members m ON ec.member_id = m.id
        WHERE ec.member_id = :memberId

        UNION ALL

        SELECT 
            tc.id AS comment_id,
            tc.content AS content,
            tc.created_at AS created_at,
            tc.member_id AS member_id,
            m.nickname AS nickname,
            tc.talk_id AS post_id,
            'TALK' AS category
        FROM talk_comments tc
        JOIN members m ON tc.member_id = m.id
        WHERE tc.member_id = :memberId
    ) AS comments
    ORDER BY created_at DESC
    LIMIT :pageSize
    """, nativeQuery = true)
    List<Object[]> getUserComments(
            @Param("memberId") Long memberId,
            @Param("pageSize") int pageSize
    );

    // 다음 커서 이후 조회
    @Query(value = """
    SELECT * FROM (
        SELECT 
            ec.id AS comment_id,
            ec.content AS content,
            ec.created_at AS created_at,
            ec.member_id AS member_id,
            m.nickname AS nickname,
            ec.exploration_id AS post_id,
            'EXPLORATION' AS category
        FROM exploration_comments ec
        JOIN members m ON ec.member_id = m.id
        WHERE ec.member_id = :memberId AND ec.id < :cursor

        UNION ALL

        SELECT 
            tc.id AS comment_id,
            tc.content AS content,
            tc.created_at AS created_at,
            tc.member_id AS member_id,
            m.nickname AS nickname,
            tc.talk_id AS post_id,
            'TALK' AS category
        FROM talk_comments tc
        JOIN members m ON tc.member_id = m.id
        WHERE tc.member_id = :memberId AND tc.id < :cursor
    ) AS comments
    ORDER BY created_at DESC
    LIMIT :pageSize
    """, nativeQuery = true)
    List<Object[]> getUserCommentsNext(
            @Param("memberId") Long memberId,
            @Param("cursor") Long cursor,
            @Param("pageSize") int pageSize
    );
}
