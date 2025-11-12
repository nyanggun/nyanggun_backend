package org.kosa.congmouse.nyanggoon.repository;

import org.kosa.congmouse.nyanggoon.dto.TalkPictureResponseDto;
import org.kosa.congmouse.nyanggoon.entity.TalkPicture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TalkPictureRepository extends JpaRepository<TalkPicture, Long> {
    // TalkPictureRepository
    List<TalkPicture> findByTalkId(Long talkId);
}
