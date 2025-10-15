package org.kosa.congmouse.nyanggoon.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.service.PhotoBoxService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/photobox")
@RequiredArgsConstructor
@Slf4j
public class PhotoBoxController {

    public final PhotoBoxService photoBoxService;

    //사진함 게시글을 조회하는 컨트롤러 입니다.
    @GetMapping("")
    public ResponseEntity<?> getAllPhotoBoxList(@RequestParam(required = false) Long cursor){
        log.info("사진함 게시글들 조회 컨트롤러 작동 ok");
        CursorResponse<List<PhotoBoxSummaryResponseDto>> photoBoxList = photoBoxService.findAllPhotoBoxList(cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "게시물 목록 조회 성공"));
    }

    //사진함 게시글을 상세 조회하는 컨트롤러 입니다.
    @GetMapping("/{id}")
    public ResponseEntity<?> getPhotoBoxDetailById(@PathVariable Long id){
        log.info("게시글 상세 조회 컨트롤러 작동 ok");
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        PhotoBoxDetailResponseDto photoBoxDetailResponseDto = photoBoxService.findPhotoBox(id, username);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxDetailResponseDto, "사진함 게시글 조회 성공"));

    }

    //사진함 게시글을 작성하는 컨트롤러 입니다.
    //인증 필요
    @PostMapping(value = "", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createPhoto ( @RequestPart("photoData") PhotoBoxCreateRequestDto photoCreateRequestDto,
                                           @RequestPart(value = "file", required = false) MultipartFile file){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        log.info("사진함 게시글 작성, 작성자 {}", username);
        PhotoBoxDetailResponseDto photoDetailResponseDto = photoBoxService.createPhoto(photoCreateRequestDto, file, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(photoDetailResponseDto, "사진함 게시글이 작성되었습니다."));
    }


    //사진함 게시글을 수정하는 컨트롤러 입니다.
    //인증 필요
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updatePhoto(
            @PathVariable Long id,
            @RequestPart("photoData") PhotoBoxCreateRequestDto photoBoxCreateRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        PhotoBoxDetailResponseDto photoDetailResponseDto = photoBoxService.updatePhoto(id, photoBoxCreateRequestDto, file, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(photoDetailResponseDto, "사진함 게시글이 수정되었습니다."));

    }


    //사진함 게시글을 삭제하는 컨트롤러 입니다.
    //인증 필요
    @DeleteMapping("/detail/{id}")
    public ResponseEntity<Void> deletePhoto(@PathVariable Long id){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        log.info("사진함 게시글 삭제 컨트롤러 작동 ok");
        log.info("삭제할 게시글 id : {}", id);
        try {
            photoBoxService.deletPhotoBox(id, username);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }

    }

    //사진함 게시글을 북마크하는 컨트롤러 입니다.
    //인증 필요
    @PostMapping("/bookmark/{photoBoxId}")
    public ResponseEntity<?> createTalkBookmark(@PathVariable Long photoBoxId){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        photoBoxService.createPhotoBookmark(photoBoxId, username);
        // 응답 DTO 생성

        // ApiResponseDto 의 표준화된 형식으로 응답한다
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(null, "북마크 등록 완료"));
    }

    //사진함 게시글을 북마크 취소하는 컨트롤러 입니다.
    //인증 필요

    @DeleteMapping("/bookmark/{photoBoxId}")
    public ResponseEntity<Void> deleteTalkBookmark(@PathVariable Long photoBoxId){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        log.info("북마크 삭제 컨트롤러 작동 ok");
        try{
            photoBoxService.deletePhotoBookmark(photoBoxId, username);
            return ResponseEntity.noContent().build();
        }
        catch(Exception e){
            return  ResponseEntity.notFound().build();
        }
    }

    //사진함 게시글을 검색하는 컨트롤러 입니다.
    @GetMapping("/search")
    public ResponseEntity<?> findPhotoBoxWithTag(@RequestParam String keyword){

        log.info("사진함 게시글 검색 컨트롤러 작동 ok");
        List<PhotoBoxSummaryResponseDto> photoBoxList = photoBoxService.findPhotoBoxWithTag(keyword);

        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "게시물 검색 결과 조회 성공"));
    }

    //사진함 게시글을 신고하는 컨트롤러 입니다.
    //인증 필요
}

