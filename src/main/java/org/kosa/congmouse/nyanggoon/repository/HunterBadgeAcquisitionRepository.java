package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.HunterBadgeAcquisition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HunterBadgeAcquisitionRepository extends JpaRepository<HunterBadgeAcquisition, Long> {
    boolean existsByMemberIdAndHunterBadgeId(Long hunterBadgeId, Long memberId);

    @Query("SELECT hba.hunterBadge.id FROM HunterBadgeAcquisition hba WHERE hba.member.id = :memberId")
    List<Long> findBadgeIdsByMemberId(@Param("memberId") Long memberId);
}
