package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Talk, Long> {

    // 첫 페이지 조회
    @Query(value = """
    SELECT * FROM (
        SELECT 
            t.id AS post_id,
            t.title AS title,
            t.content AS content,
            t.created_at AS created_at,
            t.member_id AS member_id,
            m.nickname AS nickname,
            NULL AS related_heritage,
            t.state AS content_state,
            'TALK' AS category
        FROM talks t
        JOIN members m ON t.member_id = m.id

        UNION ALL

        SELECT 
            e.id AS post_id,
            e.title AS title,
            e.content AS content,
            e.created_at AS created_at,
            e.member_id AS member_id,
            m.nickname AS nickname,
            e.related_heritage AS related_heritage,
            e.state AS content_state,
            'EXPLORATION' AS category
        FROM explorations e
        JOIN members m ON e.member_id = m.id
    ) AS posts
    WHERE posts.member_id = :userId
    ORDER BY posts.post_id DESC
    LIMIT :pageSize
""", nativeQuery = true)
    List<Object[]> getUserPostList(@Param("userId") Long userId, @Param("pageSize") int pageSize);



    // 다음 커서 이후 게시글 조회
    @Query(value = """
    SELECT * FROM (
        SELECT 
            t.id AS post_id,
            t.title AS title,
            t.content AS content,
            t.created_at AS created_at,
            t.member_id AS member_id,
            m.nickname AS nickname,
            NULL AS related_heritage,
            t.state AS content_state,
            'TALK' AS category
        FROM talks t
        JOIN members m ON t.member_id = m.id
        WHERE t.id < :cursor

        UNION ALL

        SELECT 
            e.id AS post_id,
            e.title AS title,
            e.content AS content,
            e.created_at AS created_at,
            e.member_id AS member_id,
            m.nickname AS nickname,
            e.related_heritage AS related_heritage,
            e.state AS content_state,
            'EXPLORATION' AS category
        FROM explorations e
        JOIN members m ON e.member_id = m.id
        WHERE e.id < :cursor
    ) AS posts
    WHERE posts.member_id = :userId
    ORDER BY posts.post_id DESC
    LIMIT :pageSize
""", nativeQuery = true)
    List<Object[]> getUserPostListNext(@Param("userId") Long userId, @Param("cursor") Long cursor, @Param("pageSize") int pageSize);

    @Query(value = """
SELECT * FROM (
    SELECT 
        e.id AS post_id,
        e.title AS title,
        e.content AS content,
        e.created_at AS created_at,
        e.member_id AS author_id,
        m.nickname AS nickname,
        e.related_heritage AS related_heritage,
        e.state AS content_state,
        'EXPLORATION' AS category
    FROM exploration_bookmarks eb
    JOIN explorations e ON eb.exploration_id = e.id
    JOIN members m ON e.member_id = m.id
    WHERE eb.member_id = :userId

    UNION ALL

    SELECT
        t.id AS post_id,
        t.title AS title,
        t.content AS content,
        t.created_at AS created_at,
        t.member_id AS author_id,
        m.nickname AS nickname,
        NULL AS related_heritage,
        t.state AS content_state,
        'TALK' AS category
    FROM talk_bookmarks tb
    JOIN talks t ON tb.talk_id = t.id
    JOIN members m ON t.member_id = m.id
    WHERE tb.member_id = :userId
) AS posts
ORDER BY posts.post_id DESC
LIMIT :pageSize
""", nativeQuery = true)
    List<Object[]> getUserBookmarkList(@Param("userId") Long userId, @Param("pageSize") int pageSize);

    @Query(value = """
SELECT * FROM (
    SELECT 
        e.id AS post_id,
        e.title AS title,
        e.content AS content,
        e.created_at AS created_at,
        e.member_id AS author_id,
        m.nickname AS nickname,
        e.related_heritage AS related_heritage,
        e.state AS content_state,
        'EXPLORATION' AS category
    FROM exploration_bookmarks eb
    JOIN explorations e ON eb.exploration_id = e.id
    JOIN members m ON e.member_id = m.id
    WHERE eb.member_id = :userId AND e.id < :cursor

    UNION ALL

    SELECT
        t.id AS post_id,
        t.title AS title,
        t.content AS content,
        t.created_at AS created_at,
        t.member_id AS author_id,
        m.nickname AS nickname,
        NULL AS related_heritage,
        t.state AS content_state,
        'TALK' AS category
    FROM talk_bookmarks tb
    JOIN talks t ON tb.talk_id = t.id
    JOIN members m ON t.member_id = m.id
    WHERE tb.member_id = :userId AND t.id < :cursor
) AS posts
ORDER BY posts.post_id DESC
LIMIT :pageSize
""", nativeQuery = true)
    List<Object[]> getUserBookmarkListNext(@Param("userId") Long userId, @Param("cursor") Long cursor, @Param("pageSize") int pageSize);
}
