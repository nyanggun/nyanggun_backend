package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.ExplorationComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExplorationCommentRepository extends JpaRepository<ExplorationComment, Long> {

    Long countByExplorationId(Long id);
}
