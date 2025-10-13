package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.ExplorationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExplorationCommentRepository extends JpaRepository<ExplorationComment, Long> {

    Long countByExplorationId(Long id);

    List<ExplorationComment> findByExplorationId(Long explorationId);
}
