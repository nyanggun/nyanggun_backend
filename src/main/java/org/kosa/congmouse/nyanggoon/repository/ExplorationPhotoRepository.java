package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.ExplorationPictureResponseDto;
import org.kosa.congmouse.nyanggoon.entity.ExplorationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExplorationPhotoRepository extends JpaRepository<ExplorationPhoto, Long> {
    List<ExplorationPhoto> findByExplorationId(Long id);

    // ExplorationPhotoRepository
    List<ExplorationPhoto> findByExplorationIdOrderByIdAsc(Long explorationId);
}
