package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoBoxPictureRepository extends JpaRepository<PhotoBoxPicture, Long> {


    @Query("SELECT p FROM PhotoBoxPicture p WHERE p.photoBox.id = :photoBoxId")
    PhotoBoxPicture findByIdwithPhotoBoxId(@Param("photoBoxId") Long photoBoxId);
}
