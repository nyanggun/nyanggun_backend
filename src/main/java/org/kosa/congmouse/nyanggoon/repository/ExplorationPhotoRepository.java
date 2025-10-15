package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.ExplorationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExplorationPhotoRepository extends JpaRepository<ExplorationPhoto, Long> {
    List<ExplorationPhoto> findByExplorationId(Long id);
}
