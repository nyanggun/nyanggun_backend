package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxTag;
import org.kosa.congmouse.nyanggoon.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoBoxTagRepository extends JpaRepository<PhotoBoxTag, Long> {

    @Query("SELECT t.tag.name FROM PhotoBoxTag t WHERE t.photoBox.id = :photoBoxId")
    List<String> findTags(Long photoBoxId);

    @Modifying
    @Query("DELETE FROM PhotoBoxTag t WHERE t.photoBox.id = :photoBoxId")
    void deleteByPhotoBoxId(@Param("photoBoxId") Long photoBoxId);

    // Tag 엔티티 자체 조회
    @Query("SELECT t.tag FROM PhotoBoxTag t WHERE t.photoBox.id = :photoBoxId")
    List<Tag> findTagsByPhotoBoxId(@Param("photoBoxId") Long photoBoxId);

    // 특정 태그가 아직 다른 게시글과 연결되어 있는지 확인
    boolean existsByTag(Tag tag);

}
