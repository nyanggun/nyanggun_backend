package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoBoxBookmarkRepository extends JpaRepository<PhotoBoxBookmark, Long> {
    @Query("SELECT tb.id FROM PhotoBoxBookmark tb WHERE tb.photoBox.id = :photoBoxId")
    Long getBookmarkWithPhotoBoxId(Long photoBoxId);

    @Query("SELECT tb.photoBox.id FROM PhotoBoxBookmark tb WHERE tb.member = :member")
    List<Long> findPhotoBoxIdsByMember(@Param("member") Member member);

    @Query("SELECT tb.id FROM PhotoBoxBookmark tb WHERE tb.photoBox.id = :talkId AND tb.member.id = :memberId")
    Long getBookmarkByMemberAndTalk(@Param("memberId") Long memberId, @Param("talkId") Long talkId);

}
