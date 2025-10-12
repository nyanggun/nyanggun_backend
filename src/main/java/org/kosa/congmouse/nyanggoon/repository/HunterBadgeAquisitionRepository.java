package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.HunterBadgeAquisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HunterBadgeAquisitionRepository extends JpaRepository<HunterBadgeAquisition, Long> {
    boolean existsByMemberIdAndHunterBadgeId(Long hunterBadgeId, Long memberId);
}
