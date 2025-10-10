package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeritageRepository extends JpaRepository<HeritageEncyclopedia, Long> {

    // 특정 시도(cityCode)로 조회
    List<HeritageEncyclopedia> findByCityCode(int cityCode);

    // 이름에 특정 키워드가 포함된 문화재 조회
    List<HeritageEncyclopedia> findByNameContainingIgnoreCase(String keyword);

    // 페이지네이션 + 정렬 가능
    Page<HeritageEncyclopedia> findAll(Pageable pageable);

    // 특정 종목(subjectCode) + 시도(cityCode) 조건 조회
    List<HeritageEncyclopedia> findBySubjectCodeAndCityCode(int subjectCode, int cityCode);
}