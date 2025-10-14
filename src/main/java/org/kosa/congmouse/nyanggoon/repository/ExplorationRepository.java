package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExplorationRepository extends JpaRepository<Exploration, Long> {

    @Query("SELECT distinct e from Exploration e LEFT JOIN FETCH e.explorationPhotos")
    List<Exploration> findAllWithExplorationPhotos();


}
