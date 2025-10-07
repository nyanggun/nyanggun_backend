package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkCommentRepository extends JpaRepository<TalkComment, Long> {

//해당 게시글의 댓글들을 가져옵니다.
    @Query("SELECT c FROM TalkComment c " +
            "JOIN FETCH c.member m " +
            "JOIN FETCH c.talk t " +
            "LEFT JOIN FETCH c.parentComment p " +
            "WHERE t.id = :talkId")
    List<TalkComment> findTalkComment(@Param("talkId") Long talkId);

}
