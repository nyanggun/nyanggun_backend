package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.springframework.data.domain.Pageable;
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
            "e.relatedHeritage LIKE %:keyword% OR " +
            "e.member.nickname LIKE %:keyword%")
    List<Exploration> findByKeyword(@Param("keyword") String keyword);

    @Query("SELECT new org.kosa.congmouse.nyanggoon.dto.ExplorationDetailDto(" +
            "e.id, " +
            "e.createdAt, " +
            "e.title, " +
            "e.content," +
            "e.relatedHeritage," +
            "e.member.id," +
            "e.member.nickname, " +
            "count(DISTINCT b.id), " +
            "count(DISTINCT c.id)" +
            ") " +
            "FROM Exploration e " +
            "LEFT JOIN e.member m " +
            "LEFT JOIN ExplorationBookmark b ON b.exploration = e " + // 엔티티 관계가 아닌 직접 조인
            "LEFT JOIN ExplorationComment c ON c.exploration = e " +
            "GROUP BY e.id " +
            "ORDER BY e.createdAt DESC"
    )
    List<ExplorationDetailDto> findAllWithBookmarkCountAndCommentCounts();


    //북마크 순으로 탐방기를 가져오는 메소드 입니다.
    @Query("SELECT e FROM Exploration e JOIN ExplorationBookmark eb ON e.id = eb.exploration.id GROUP BY e.id ORDER BY COUNT(eb.id) DESC ")
    List<Exploration> findExplorationTop4ByBookmarkCount(Pageable pageable);

    @Query("SELECT e FROM Exploration e ORDER BY e.createdAt DESC")
    List<Exploration> findLatestExplorations(Pageable latestPageable);
}
