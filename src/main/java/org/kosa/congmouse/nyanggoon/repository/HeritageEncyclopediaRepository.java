package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.HeritageEncyclopediaResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeritageEncyclopediaRepository extends JpaRepository<HeritageEncyclopedia, Long> {

    // 특정 시도(cityCode)로 조회
    List<HeritageEncyclopedia> findByCityCode(int cityCode);

    // 이름에 특정 키워드가 포함된 문화재 조회
    List<HeritageEncyclopedia> findByNameContainingIgnoreCase(String keyword);

    // 페이지네이션 + 정렬 가능
    Page<HeritageEncyclopedia> findAll(Pageable pageable);

    // 특정 종목(subjectCode) + 시도(cityCode) 조건 조회
    List<HeritageEncyclopedia> findBySubjectCodeAndCityCode(int subjectCode, int cityCode);

    // 검색기능 + 페이지
    @Query("""
            SELECT h FROM HeritageEncyclopedia h
            WHERE LOWER(h.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(h.address) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(h.content) LIKE LOWER(CONCAT('%', :keyword, '%'))
                OR LOWER(h.period) LIKE LOWER(CONCAT('%', :keyword, '%'))
            ORDER BY h.name ASC
            """)
    Page<HeritageEncyclopedia> searchHeritageEncyclopedia(@Param("keyword") String keyword, Pageable pageable);

}