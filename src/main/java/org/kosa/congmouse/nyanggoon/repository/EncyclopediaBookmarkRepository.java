package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkDto;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EncyclopediaBookmarkRepository extends JpaRepository<EncyclopediaBookmark, Long> {

    boolean existsByMemberIdAndHeritageEncyclopediaId(Long memberId, Long heritageEncyclopediaId);

    long countByHeritageEncyclopediaId(Long heritageEncyclopediaId);

    Optional<EncyclopediaBookmark> findByMemberAndHeritageEncyclopedia(Member member, HeritageEncyclopedia heritageEncyclopedia);
}
