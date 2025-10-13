package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.ExplorationBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExplorationBookmarkRepository extends JpaRepository<ExplorationBookmark, Long> {
    /**
     * exploration의 id를 기반으로 북마크 수를 계산합니다.
     * @param explorationId 탐방기 ID
     * @return 해당 탐방기에 대한 북마크 개수
     */
    long countByExplorationId(Long explorationId);

    ExplorationBookmark findByMemberIdAndExplorationId(Long memberId, Long explorationId);

    Boolean existsByMemberIdAndExplorationId(Long memberId, Long explorationId);

    void deleteByMemberIdAndExplorationId(Long memberId, Long explorationId);
}
