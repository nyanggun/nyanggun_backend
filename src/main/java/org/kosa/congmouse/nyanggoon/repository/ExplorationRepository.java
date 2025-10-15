package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

    @Query("SELECT distinct e from Exploration e LEFT JOIN FETCH e.explorationPhotos")
    List<Exploration> findAllWithExplorationPhotos();

    @Query("SELECT e FROM Exploration e WHERE " +
            "e.title LIKE %:keyword% OR " +
            "e.content LIKE %:keyword% OR " +
            "e.member.nickname LIKE %:keyword%")
    List<Exploration> findByKeyword(@Param("keyword") String keyword);
}
