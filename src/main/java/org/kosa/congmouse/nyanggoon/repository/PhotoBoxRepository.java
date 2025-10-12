package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
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

}
