package org.kosa.congmouse.nyanggoon.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "문화재 사진함", description = "문화재 사진함 컨트롤러 입니다.")
public class PhotoBoxController {

    public final PhotoBoxService photoBoxService;

    //사진함 게시글을 조회하는 컨트롤러 입니다.
    @GetMapping("")
    @Operation(summary = "사진함 게시글을 조회하는 컨트롤러", description = "사진함 게시글을 조회합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 조회했습니다."))
    public ResponseEntity<?> getAllPhotoBoxList( @Parameter(description = "커서 (마지막으로 가져오는 게시글의 id)", example = "") @RequestParam(required = false) Long cursor){
        log.info("사진함 게시글들 조회 컨트롤러 작동 ok");
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = photoBoxService.findAllPhotoBoxList(cursor);
        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "게시물 목록 조회 성공"));
    }

    //사진함 게시글을 상세 조회하는 컨트롤러 입니다.
    @GetMapping("/{id}")
    @Operation(summary = "사진함 게시글을 상세 조회하는 컨트롤러", description = "사진함 게시글을 상세 조회합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글의 상세를 조회했습니다."))
    public ResponseEntity<?> getPhotoBoxDetailById( @Parameter(description = "게시글 id", example = "")@PathVariable Long id){
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
    @Operation(summary = "사진함 게시글을 작성하는 컨트롤러", description = "사진함 게시글을 작성합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 작성했습니다."))
    @PostMapping(value = "")
    public ResponseEntity<?> createPhoto (  @RequestBody PhotoBoxCreateRequestDto photoCreateRequestDto
                                            ){
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();
        log.info("사진함 게시글 작성, 작성자 {}", username);
        PhotoBoxDetailResponseDto photoDetailResponseDto = photoBoxService.createPhoto(photoCreateRequestDto, username);

        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(photoDetailResponseDto, "사진함 게시글이 작성되었습니다."));
    }


    //사진함 게시글을 수정하는 컨트롤러 입니다.
    //인증 필요
    @Operation(summary = "사진함 게시글을 수정하는 컨트롤러", description = "사진함 게시글을 수정합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 수정했습니다."))
    @PutMapping(value = "/{id}")
    public ResponseEntity<?> updatePhoto(
            @Parameter(description = "게시글 id", example = "")
            @PathVariable Long id,
            @RequestBody PhotoBoxCreateRequestDto photoBoxCreateRequestDto) {

        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        PhotoBoxDetailResponseDto photoDetailResponseDto = photoBoxService.updatePhoto(id, photoBoxCreateRequestDto, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponseDto.success(photoDetailResponseDto, "사진함 게시글이 수정되었습니다."));

    }


    //사진함 게시글을 삭제하는 컨트롤러 입니다.
    //인증 필요
    @Operation(summary = "사진함 게시글을 삭제하는 컨트롤러", description = "사진함 게시글을 삭제합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 삭제했습니다."))
    @DeleteMapping("/detail/{id}")
    public ResponseEntity<Void> deletePhoto(@Parameter(description = "게시글 id", example = "")@PathVariable Long id){
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
    @Operation(summary = "사진함 게시글을 북마크하는 컨트롤러", description = "사진함 게시글을 북마크합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 북마크했습니다."))
    @PostMapping("/bookmark/{photoBoxId}")
    public ResponseEntity<?> createTalkBookmark(@Parameter(description = "게시글 id", example = "")@PathVariable Long photoBoxId){
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
    @Operation(summary = "사진함 게시글을 북마크 취소하는 컨트롤러", description = "사진함 게시글의 북마크를 취소합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 북마크를 취소했습니다."))
    @DeleteMapping("/bookmark/{photoBoxId}")
    public ResponseEntity<Void> deleteTalkBookmark(@Parameter(description = "게시글 id", example = "")@PathVariable Long photoBoxId){
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
    @Operation(summary = "사진함 게시글을 검색하는 컨트롤러" , description = "사진함 게시글을 검색합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 검색했습니다."))
    public ResponseEntity<?> findPhotoBoxWithTag(@Parameter(description = "키워드", example = "")@RequestParam String keyword, @Parameter(description = "커서 (마지막으로 가져오는 게시글의 id)", example = "")@RequestParam(required = false) Long cursor){

        log.info("사진함 게시글 검색 컨트롤러 작동 ok");
        log.info("받은 요청 : keyword= {} cursor={} ", keyword, cursor);
        PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> photoBoxList = photoBoxService.findPhotoBoxWithTag(keyword, cursor);

        return ResponseEntity.ok(ApiResponseDto.success(photoBoxList, "게시물 검색 결과 조회 성공"));
    }

    //사진함 게시글을 신고하는 컨트롤러 입니다.
    //인증 필요
    @Operation(summary = "사진함 게시글을 신고하는 컨트롤러" , description = "사진함 게시글을 신고합니다.")
    @ApiResponses(@ApiResponse(responseCode = "200", description = "정상적으로 사진함 게시글을 신고했습니다."))
    @PostMapping("/reports")
    public ResponseEntity<?> postPhotoBoxReport( @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "신고 내용 및 게시글 id, 회원 id",
            required = true,
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = ReportCreateRequestDto.class)
            )
    )@RequestBody ReportCreateRequestDto reportCreateRequestDto){
        ReportResponseDto explorationReportResponseDto = photoBoxService.createPhotoBoxReport(reportCreateRequestDto);
        return ResponseEntity.ok(ApiResponseDto.success(explorationReportResponseDto, "신고 완료"));
    }
}

