package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoBoxRepository extends JpaRepository<PhotoBox, Long> {
    // 북마크 개수
    @Query("SELECT COUNT(b) FROM PhotoBoxBookmark b WHERE b.photoBox.id = :photoBoxId")
    long countBookmarksByPhotoId(@Param("photoBoxId") Long photoBoxId);


    @Query("SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(" +
            "p.photoBox.id, p.id, p.path, p.createdAt) " +
            "FROM PhotoBoxPicture p")
    List<PhotoBoxSummaryResponseDto> findAllPictures();

    //사진함 조회 쿼리문 입니다.
    //무한스크롤로 구현했습니다.
    // 첫 페이지 또는 cursor null
    @Query("""
        SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
            p.photoBox.id, p.id, p.path, p.createdAt
        )
        FROM PhotoBoxPicture p
        ORDER BY p.id DESC
    """)
    List<PhotoBoxSummaryResponseDto> findPhotoBox(Pageable pageable);

    // cursor 이후 데이터
    @Query("""
        SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
            p.photoBox.id, p.id, p.path, p.createdAt
        )
        FROM PhotoBoxPicture p
        WHERE p.id < :cursorId
        ORDER BY p.id DESC
    """)
    List<PhotoBoxSummaryResponseDto> findPhotoBoxNext(@Param("cursorId") Long cursorId, Pageable pageable);


//    @Query("""
//    SELECT DISTINCT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
//        p.photoBox.id, p.id, p.path, p.createdAt
//    )
//    FROM PhotoBoxPicture p
//    JOIN p.photoBox.tags pt
//    JOIN pt.tag t
//    WHERE t.name LIKE CONCAT('%', :keyword, '%')
//    ORDER BY p.createdAt DESC
//    """)
//    List<PhotoBoxSummaryResponseDto> findPhotoBoxPicturesWithTag(@Param("keyword") String keyword);

    //태그를 사용하여 검색합니다.
    //무한 스크롤로 구현합니다.
    //첫 페이지 (cursor = null)
    @Query("""
    SELECT DISTINCT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        p.photoBox.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxPicture p
    JOIN p.photoBox.tags pt
    JOIN pt.tag t
    WHERE (t.name LIKE CONCAT('%', :keyword, '%')
    OR p.photoBox.title LIKE CONCAT('%', :keyword, '%'))
    ORDER BY p.id DESC
""")
    List<PhotoBoxSummaryResponseDto> findPhotoBoxWithTag(
            @Param("keyword") String keyword,
            Pageable pageable
    );
    // 다음 페이지 (cursor != null)
    @Query("""
    SELECT DISTINCT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        p.photoBox.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxPicture p
    JOIN p.photoBox.tags pt
    JOIN pt.tag t
    WHERE (t.name LIKE CONCAT('%', :keyword, '%')
        OR p.photoBox.title LIKE CONCAT('%', :keyword, '%'))
      AND (:cursorId IS NULL OR p.id < :cursorId)
    ORDER BY p.id DESC
""")
    List<PhotoBoxSummaryResponseDto> findPhotoBoxNextWithTag(
            @Param("keyword") String keyword,
            @Param("cursorId") Long cursorId,
            Pageable pageable
    );

}
