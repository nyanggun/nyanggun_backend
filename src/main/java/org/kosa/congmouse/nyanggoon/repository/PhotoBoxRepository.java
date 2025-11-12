package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.springframework.data.domain.PageRequest;
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

    //최다 북마크 가져오는 로직
    @Query("SELECT p FROM PhotoBox p LEFT JOIN PhotoBoxBookmark b ON b.photoBox.id = p.id GROUP BY p.id ORDER BY COUNT(b.id) DESC")
    List<PhotoBox> findMostPhotoBoxBookmark(Pageable pageable);

    PhotoBox findTopByOrderByCreatedAtDesc();

    //회원이 작성한 사진함 가져오기
    @Query("""
    SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        p.photoBox.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxPicture p
    JOIN p.photoBox pb
    JOIN pb.member u
    WHERE u.id = :userId
    ORDER BY p.id DESC
    """)
    List<PhotoBoxSummaryResponseDto> findPhotoBoxById(@Param("userId")Long userId, Pageable pageable);
    //회원이 작성한 사진함 가져오기 다음 로직
    @Query("""
    SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        p.photoBox.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxPicture p
    JOIN p.photoBox pb
    JOIN pb.member u
    WHERE u.id = :userId AND p.id < :cursorId
    ORDER BY p.id DESC
    """)
    List<PhotoBoxSummaryResponseDto> findPhotoBoxNextById(@Param("userId")Long userId, @Param("cursorId")Long cursorId, Pageable pageable);

    @Query("""
    SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        pb.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxBookmark b
    JOIN b.photoBox pb
    JOIN PhotoBoxPicture p ON p.photoBox = pb
    WHERE b.member.id = :userId
    ORDER BY p.id DESC
""")
    //회원이 북마크한 사진함 가져오기
    List<PhotoBoxSummaryResponseDto> findPhotoBoxBookmarkById(@Param("userId")Long userId, PageRequest of);
    @Query("""
    SELECT new org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto(
        pb.id, p.id, p.path, p.createdAt
    )
    FROM PhotoBoxBookmark b
    JOIN b.photoBox pb
    JOIN PhotoBoxPicture p ON p.photoBox = pb
    WHERE b.member.id = :userId
      AND p.id < :cursorId
    ORDER BY p.id DESC
""")
    //회원이 북마크한 사진함 가져오기 다음 로직
    List<PhotoBoxSummaryResponseDto> findPhotoBoxBookmarkNextById(@Param("userId")Long userId, @Param("cursorId") Long cursorId, PageRequest of);

}
