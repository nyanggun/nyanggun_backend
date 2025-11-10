package org.kosa.congmouse.nyanggoon.service;

import org.junit.jupiter.api.Test;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxCreateRequestDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.PhotoBox;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxBookmark;
import org.kosa.congmouse.nyanggoon.entity.PhotoBoxPicture;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.kosa.congmouse.nyanggoon.repository.PhotoBoxBookmarkRepository;
import org.kosa.congmouse.nyanggoon.repository.PhotoBoxRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.Query;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@SpringBootTest
@Transactional
public class PhotoBoxServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PhotoBoxRepository photoBoxRepository;

    @Autowired
    private PhotoBoxBookmarkRepository photoBoxBookmarkRepository;

    @Autowired
    private PhotoBoxService photoBoxService;

    //사진함을 작성하는 서비스 테스트

    @Test
    public void createPhotoBoxTest(){
        // given
        Member member = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

        // Mock 파일 생성 (테스트용 이미지 파일 시뮬레이션)
        MockMultipartFile file = new MockMultipartFile(
                "file",                            // 필드 이름 (Multipart 요청에서 key 역할)
                "test-image.jpg",                  // 원본 파일 이름
                "image/jpeg",                      // Content-Type
                "dummy image data".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        PhotoBoxCreateRequestDto photoBox = PhotoBoxCreateRequestDto.builder()
                .title("사진함 제목")
                .memberid(member.getId())
                .tags(new ArrayList<>())
                .relatedHeritage("연관된 문화재")
                .build();

        //when
        photoBoxService.createPhoto(photoBox, file, "user1@example.com");

        //then
        assertThat(photoBoxRepository.findAll()).isNotEmpty();


    }

    //사진함을 북마크하는 서비스 테스트

    @Test
    public void bookmarkPhotoBoxTest(){
        // given
        Member member = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

        // Mock 파일 생성 (테스트용 이미지 파일 시뮬레이션)
        MockMultipartFile file = new MockMultipartFile(
                "file",                            // 필드 이름 (Multipart 요청에서 key 역할)
                "test-image.jpg",                  // 원본 파일 이름
                "image/jpeg",                      // Content-Type
                "dummy image data".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        PhotoBoxCreateRequestDto photoBox = PhotoBoxCreateRequestDto.builder()
                .title("사진함 제목")
                .memberid(member.getId())
                .tags(new ArrayList<>())
                .relatedHeritage("연관된 문화재")
                .build();

        //when
        PhotoBoxDetailResponseDto p = photoBoxService.createPhoto(photoBox, file, "user1@example.com");
        photoBoxService.createPhotoBookmark(p.getId(), "user1@example.com");

        //then
        assertThat(photoBoxBookmarkRepository.findPhotoBoxIdsByMember(member)).size().isEqualTo(1);


    }


    //사진함을 수정하는 서비스 테스트
@Test
    public void updatePhotoBoxTest(){
        // given
        Member member = memberRepository.findByEmail("user1@example.com")
                .orElseThrow(() -> new IllegalStateException("테스트용 유저가 DB에 존재하지 않습니다."));

        // Mock 파일 생성 (테스트용 이미지 파일 시뮬레이션)
        MockMultipartFile file = new MockMultipartFile(
                "file",                            // 필드 이름 (Multipart 요청에서 key 역할)
                "test-image.jpg",                  // 원본 파일 이름
                "image/jpeg",                      // Content-Type
                "dummy image data".getBytes(StandardCharsets.UTF_8) // 파일 내용
        );

        PhotoBoxCreateRequestDto photoBox = PhotoBoxCreateRequestDto.builder()
                .title("사진함 제목")
                .memberid(member.getId())
                .tags(new ArrayList<>())
                .relatedHeritage("연관된 문화재")
                .build();

        //when
        PhotoBoxDetailResponseDto p = photoBoxService.createPhoto(photoBox, file, "user1@example.com");

        PhotoBoxCreateRequestDto photoBox2 = PhotoBoxCreateRequestDto.builder()
                .title("사진함 제목222")
                .memberid(member.getId())
                .tags(new ArrayList<>())
                .relatedHeritage("연관된 문화재222")
                .build();

        PhotoBoxDetailResponseDto p2 = photoBoxService.updatePhoto(p.getId(), photoBox2, file, "user1@example.com");

        //then
        PhotoBox updatedEntity = photoBoxRepository.findById(p.getId())
                .orElseThrow(() -> new IllegalStateException("수정된 사진함이 존재하지 않습니다."));

        assertThat(updatedEntity.getTitle()).isEqualTo("사진함 제목222");
        assertThat(updatedEntity.getRelatedHeritage()).isEqualTo("연관된 문화재222");

    }
}
