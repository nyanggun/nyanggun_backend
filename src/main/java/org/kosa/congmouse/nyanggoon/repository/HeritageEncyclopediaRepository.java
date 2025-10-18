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

    // 메시지 전체가 아닌, DB에 저장된 문화재 이름이 메시지 안에 포함되어 있는지를 확인하는 로직을 유지하면서,
    // 그 결과가 1개 이상일 경우
    @Query("SELECT h FROM HeritageEncyclopedia h WHERE :message LIKE CONCAT('%', h.name, '%')")
    List<HeritageEncyclopedia> findHeritageByMessageContent(@Param("message") String message);

    /**
     * AI 응답 문자열에서 문화재 이름을 찾아 단일 엔티티를 반환합니다.
     * AI의 긴 응답 문자열(`:aiResponse`) 내에 DB에 저장된 문화재 이름이 포함되어 있는지 확인합니다.
     * 결과가 여러 개일 경우 첫 번째 결과만 반환합니다.
     */
    @Query(value = "SELECT * FROM heritage_encyclopedias h WHERE :aiResponse LIKE CONCAT('%', h.name, '%') LIMIT 1", nativeQuery = true)
    HeritageEncyclopedia findSingleHeritageByNameInString(@Param("aiResponse") String aiResponse);

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

    @Query("""
            SELECT h
            FROM HeritageEncyclopedia h
            ORDER BY h.name ASC
            """)
    Page<HeritageEncyclopedia> findAllOrderByName(Pageable pageable);

    @Query("""
            SELECT h
            FROM HeritageEncyclopedia h
            LEFT JOIN EncyclopediaBookmark b ON b.heritageEncyclopedia = h
            GROUP BY h
            ORDER BY COUNT(b) DESC
            """)
    Page<HeritageEncyclopedia> findAllOrderByPopular(Pageable pageable);

    //북마크 수에 따라 상위 4개를 가져오는 메소드 입니다.
    @Query("SELECT h FROM HeritageEncyclopedia h JOIN EncyclopediaBookmark eb ON h.id = eb.heritageEncyclopedia.id GROUP BY h.id ORDER BY COUNT(eb.id) DESC ")
    List<HeritageEncyclopedia> findTop4ByBookmarkCount(Pageable pageable);

}