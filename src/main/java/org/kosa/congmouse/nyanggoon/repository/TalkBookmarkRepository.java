package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TalkBookmarkRepository extends JpaRepository<TalkBookmark, Long> {

    @Query("SELECT tb.id FROM TalkBookmark tb WHERE tb.talk.id = :talkId")
    Long getBookmarkWithTalkId(Long talkId);

    @Query("SELECT tb.talk.id FROM TalkBookmark tb WHERE tb.member = :member")
    List<Long> findTalkIdsByMember(@Param("member") Member member);

    @Query("SELECT tb.id FROM TalkBookmark tb WHERE tb.talk.id = :talkId AND tb.member.id = :memberId")
    Long getBookmarkByMemberAndTalk(@Param("memberId") Long memberId, @Param("talkId") Long talkId);

}
