package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.HunterBadgeAquisition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HunterBadgeAquisitionRepository extends JpaRepository<HunterBadgeAquisition, Long> {
    boolean findByMemberIdAndBadgeId(Long badgeId, Long memberId);
}
