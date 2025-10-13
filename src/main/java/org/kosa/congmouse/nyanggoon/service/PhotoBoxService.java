package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxDetailResponseDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxSummaryResponseDto;
import org.kosa.congmouse.nyanggoon.dto.PhotoBoxCreateRequestDto;
import org.kosa.congmouse.nyanggoon.entity.*;
import org.kosa.congmouse.nyanggoon.repository.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
public class PhotoBoxService {
    private final MemberRepository memberRepository;
    private final PhotoBoxRepository photoBoxRepository;
    private final TagRepository tagRepository;
    private final PhotoBoxTagRepository photoBoxTagRepository;
    private final PhotoBoxPictureRepository photoBoxPictureRepository;
    private final PhotoBoxBookmarkRepository photoBoxBookmarkRepository;

    //사진함 게시글들을 모두 조회하는 메소드 입니다.
    public List<PhotoBoxSummaryResponseDto> findAllPhotoBoxList() {

        List<PhotoBoxSummaryResponseDto> PhotoBoxAll = photoBoxRepository.findAllPictures();
        return PhotoBoxAll;
    }

    //사진함의 사진 상세를 조회하는 메소드 입니다.
    public PhotoBoxDetailResponseDto findPhotoBox(Long id, String username) {
        log.info("사진함 게시글을 불러옵니다. {}", id);
        PhotoBox photoBox = photoBoxRepository.findById(id).orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        PhotoBoxPicture photoBoxPicture = photoBoxPictureRepository.findByIdwithPhotoBoxId(id);
        List<String> photoBoxTag = photoBoxTagRepository.findTags(id);
        //북마크 개수 카운트하기
        Long bookmarkCounts = photoBoxRepository.countBookmarksByPhotoId(id); //북마크 개수
        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용
        //북마크여부 가져오기
        boolean isBookmarked = false;
        if (member != null) {
            // 해당 게시글만 확인
            isBookmarked = photoBoxBookmarkRepository.getBookmarkByMemberAndTalk(member.getId(), photoBox.getId()) != null;

        }

        return PhotoBoxDetailResponseDto.from(photoBox, photoBoxPicture, photoBoxTag, bookmarkCounts, isBookmarked);

    }

    //사진함 게시글을 작성하는 메소드 입니다.
    @Transactional
    public PhotoBoxDetailResponseDto createPhoto(PhotoBoxCreateRequestDto photoBoxCreateRequestDto, MultipartFile file, String username){
        log.info("사진함 게시글 작성 시작");

        //해당 회원이 있는 지 확인한다. (이메일로 확인)
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        //게시글 정보 객체 생성
        PhotoBox photoBox = PhotoBox.builder()
                .title(photoBoxCreateRequestDto.getTitle())
                .relatedHeritage(photoBoxCreateRequestDto.getRelatedHeritage())
                .member(member)
                .build();

        PhotoBox savePhotoBox = photoBoxRepository.save(photoBox);

        //사진 저장
        String originalName = file.getOriginalFilename(); // 사용자가 업로드한 파일 이름
        long size = file.getSize();                       // 파일 크기
        String extension = FilenameUtils.getExtension(originalName); // 확장자 추출 (commons-io)

        String filePath = null;
        if (file != null && !file.isEmpty()) {
            filePath = saveFile(file, photoBox.getId(), extension);
        }

        PhotoBoxPicture photoBoxPicture = PhotoBoxPicture.builder()
                .photoBox(photoBox)
                .originalName(originalName)
                .savedName(filePath)
                .path("/uploads/" + filePath)
                .size(size)
                .fileExtension(extension)
                .build();

        PhotoBoxPicture savePhotoBoxPicture = photoBoxPictureRepository.save(photoBoxPicture);

        //사진 태그 객체 생성
        for (String name : photoBoxCreateRequestDto.getTags()) {
            // '#' 제거
            //String cleanName = tagName.startsWith("#") ? tagName.substring(1) : tagName;
            // 기존 태그 있으면 가져오고, 없으면 생성
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));

            // 교차 테이블에 저장
            photoBoxTagRepository.save(new PhotoBoxTag(photoBox, tag));
        }
        //북마크 개수 카운트하기
        Long bookmarkCounts = photoBoxRepository.countBookmarksByPhotoId(photoBox.getId()); //북마크 개수

        //북마크횟수 0 (처음 작성이므로)
        boolean isBookmarked = false;

        return PhotoBoxDetailResponseDto.from(photoBox, photoBoxPicture, photoBoxCreateRequestDto.getTags(), bookmarkCounts, isBookmarked);

    }

    //사진을 업로드하는 메소드 입니다.
    private String saveFile(MultipartFile file, Long photoBoxId, String extension) {
        // Windows 바탕화면 경로 (현재 사용자 기준)
//        String userHome = System.getProperty("user.home"); // C:/Users/사용자명
//        String uploadDir = userHome + "/Desktop/uploads/"; // 바탕화면 하위 uploads 폴더
        // 서버 루트 기준 uploads 폴더
        String uploadDir = System.getProperty("user.dir") + "/uploads/";

        //저장 파일 명 :
        String savedFileName = String.valueOf(photoBoxId) + "."+ extension;

        //폴더 없으면 폴더 만들기
        File uploadPath = new File(uploadDir);
        if (!uploadPath.exists()) {
            uploadPath.mkdirs();
        }
        //파일 객체 생성
        File dest = new File(uploadDir, savedFileName);

        //파일 저장
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패", e);
        }

        return savedFileName;
    }

    //사진 게시글을 수정하는 메소드 입니다.
    @Transactional
    public PhotoBoxDetailResponseDto updatePhoto(
            Long photoId,
            PhotoBoxCreateRequestDto photoBoxCreateRequestDto,
            MultipartFile file,
            String username) {

        log.info("사진함 게시글 수정 시작: photoId = {}", photoId);

        // 게시글 조회 및 작성자 검증
        PhotoBox photoBox = photoBoxRepository.findById(photoId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!photoBox.getMember().getEmail().equals(username)) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }

        // 게시글 정보 수정
        photoBox.update(photoBoxCreateRequestDto.getTitle(), photoBoxCreateRequestDto.getRelatedHeritage());
        PhotoBoxPicture existingPicture = photoBoxPictureRepository.findByIdwithPhotoBoxId(photoBox.getId());
        
        log.info("게시글 정보 수정 완료");
        
        // 사진 파일 수정 처리
        if (file != null && !file.isEmpty()) {

            // 기존 파일 삭제
            deleteFile(existingPicture.getSavedName());

            // 새 파일 저장
            String extension = FilenameUtils.getExtension(file.getOriginalFilename());
            String savedName = saveFile(file, photoBox.getId(), extension);
            String path = "/uploads/" + savedName;

            existingPicture.update(
                    file.getOriginalFilename(),
                    savedName,
                    path,
                    file.getSize(),
                    extension
            );
        }


        // 기존 태그 연결 전부 삭제
        List<Tag> oldTags = photoBoxTagRepository.findTagsByPhotoBoxId(photoBox.getId());
        photoBoxTagRepository.deleteByPhotoBoxId(photoBox.getId());

        // 고아 태그 정리 (다른 게시글에서 더 이상 안 쓰이는 태그 삭제)
        for (Tag oldTag : oldTags) {
            boolean stillUsed = photoBoxTagRepository.existsByTag(oldTag);
            if (!stillUsed) {
                tagRepository.delete(oldTag);
                log.info("사용되지 않는 태그 삭제: {}", oldTag.getName());
            }
        }

        // 새 태그 등록
        for (String name : photoBoxCreateRequestDto.getTags()) {
            // '#' 제거
            //String cleanName = tagName.startsWith("#") ? tagName.substring(1) : tagName;
            // 기존 태그 있으면 가져오고, 없으면 생성
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));

            // 교차 테이블에 저장
            photoBoxTagRepository.save(new PhotoBoxTag(photoBox, tag));

        }

            // 북마크 개수 조회
            Long bookmarkCounts = photoBoxRepository.countBookmarksByPhotoId(photoBox.getId());
            //유저 조회
            Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용
        //북마크여부 가져오기
        boolean isBookmarked = false;
        if (member != null) {
            // 해당 게시글만 확인
            isBookmarked = photoBoxBookmarkRepository.getBookmarkByMemberAndTalk(member.getId(), photoBox.getId()) != null;

        }

            log.info("사진함 게시글 수정 완료: photoId = {}", photoId);

            return PhotoBoxDetailResponseDto.from(photoBox, existingPicture, photoBoxCreateRequestDto.getTags(), bookmarkCounts, isBookmarked);

    }


    //사진 파일을 삭제하는 메소드 입니다.
    private void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        // 저장 경로 설정 (create 시 saveFile()과 동일한 경로 구조여야 함)
        Path filePath = Paths.get("uploads").resolve(fileName);

        try {
            Files.deleteIfExists(filePath); // 파일이 존재할 경우만 삭제
            log.info("파일 삭제 성공: {}", filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }

    //사진함 게시글을 삭제하는 메소드 입니다.
    @Transactional
    public void deletPhotoBox(Long photoBoxId, String username) {

        // 게시글 조회 및 작성자 검증
        log.info("삭제할 사진함 게시글을 불러옵니다. {}", photoBoxId);
        PhotoBox photoBox = photoBoxRepository.findById(photoBoxId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!photoBox.getMember().getEmail().equals(username)) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }
        //사진 파일 삭제
        PhotoBoxPicture existingPicture = photoBoxPictureRepository.findByIdwithPhotoBoxId(photoBoxId);
        if (existingPicture != null) {
            deleteFile(existingPicture.getSavedName());
        }

        // 기존 태그 연결 (태그 교차 테이블은 cascade 옵션으로 자동 삭제됨)
        List<Tag> oldTags = photoBoxTagRepository.findTagsByPhotoBoxId(photoBox.getId());
        photoBoxTagRepository.deleteByPhotoBoxId(photoBox.getId());

        // 고아 태그 정리 (다른 게시글에서 더 이상 안 쓰이는 태그 삭제)
        for (Tag oldTag : oldTags) {
            boolean stillUsed = photoBoxTagRepository.existsByTag(oldTag);
            if (!stillUsed) {
                tagRepository.delete(oldTag);
                log.info("사용되지 않는 태그 삭제: {}", oldTag.getName());
            }
        }
        //사진함 게시글 삭제
        photoBoxRepository.delete(photoBox);
        log.info("사진함 게시글 삭제 완료");

    }

    //사진함을 북마크하는 메소드 입니다.
    @Transactional
    public void createPhotoBookmark(Long photoBoxId, String username) {
        // 사용자 조회
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        // 게시글 조회
        PhotoBox photoBox = photoBoxRepository.findById(photoBoxId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //북마크 객체 생성
        PhotoBoxBookmark photoBoxBookmark = PhotoBoxBookmark.builder()
                .member(member)   // 현재 로그인한 사용자 엔티티
                .photoBox(photoBox)       // 북마크할 게시글 엔티티
                .build();
        //북마크 저장
        photoBoxBookmarkRepository.save(photoBoxBookmark);
    }

    //사진함을 북마크 취소하는 메소드 입니다.
    @Transactional
    public void deletePhotoBookmark(Long photoBoxId, String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        // 게시글 조회
        PhotoBox photoBox = photoBoxRepository.findById(photoBoxId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        Long bookmarkId = photoBoxBookmarkRepository.getBookmarkWithPhotoBoxId(photoBoxId);

        photoBoxBookmarkRepository.deleteById(bookmarkId);

    }

//    //사진함에서 태그로 검색하는 메소드 입니다.
//    @Transactional
//    public List<PhotoBoxSummaryResponseDto> findPhotoBoxWithTag(String keyword) {
//        List<PhotoBoxSummaryResponseDto> findPhotoBoxWithTag = photoBoxRepository.findPhotoBoxPicturesWithTag(keyword);
//        return findPhotoBoxWithTag;
//    }
}
