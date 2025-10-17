package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.HunterBadgeAcquireResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HunterBadge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HunterBadgeRepository extends JpaRepository<HunterBadge, Long> {

}
