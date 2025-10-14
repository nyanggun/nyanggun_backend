package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.EncyclopediaBookmarkRequestDto;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EncyclopediaBookmarkRepository extends JpaRepository<EncyclopediaBookmark, Long> {

    boolean existsByMemberIdAndHeritageEncyclopediaId(Long memberId, Long heritageEncyclopediaId);

    long countByHeritageEncyclopediaId(Long heritageEncyclopediaId);

//    EncyclopediaBookmarkRequestDto saveByHeritageEncyclopediaIdAndMemberId(Long heritageEncyclopediaId, Long memberId);
}
