package org.kosa.congmouse.nyanggoon.repository;

import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxTag;
import org.kosa.congmouse.nyanggoon.service.PhotoBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
public class PhotoBoxRepositoryTest {

    @Autowired
    PhotoBoxService photoBoxService;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PhotoBoxRepository photoBoxRepository;

    @Autowired
    PhotoBoxPictureRepository photoBoxPictureRepository;

    //'사진함을 저장하는 테스트 입니다.
    @Test
    public void savePhotoBoxTest() {
        Member member = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        List<PhotoBoxTag> tags = null;

        PhotoBox photoBox = PhotoBox.builder().title("제목").tags(tags).member(member).relatedHeritage("관련문화재").build();

        PhotoBox saved = photoBoxRepository.save(photoBox);

        PhotoBox find = photoBoxRepository.findById(saved.getId()).orElseThrow();

        assertEquals("제목", find.getTitle());
    }
}
