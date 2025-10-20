package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.*;
import org.kosa.congmouse.nyanggoon.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ExplorationService {
    private final ExplorationRepository explorationRepository;
    private final ExplorationBookmarkRepository explorationBookmarkRepository;
    private final ExplorationCommentRepository explorationCommentRepository;
    private final ExplorationPhotoRepository explorationPhotoRepository;
    private final MemberRepository memberRepository;
    private final ReportRepository reportRepository;

    @Value("${exploration.img.path}")
    private String uploadDirPath;

    @Transactional
    public ExplorationDetailDto createExploration(ExplorationCreateDto explorationCreateDto, List<MultipartFile> imageFileList) throws IOException {
        // Exploration 게시물 저장
        File uploadDir = new File(uploadDirPath);
        if(!uploadDir.exists()){
           uploadDir.mkdirs();
        }
        Exploration exploration = Exploration.builder()
                .title(explorationCreateDto.getTitle())
                .content(explorationCreateDto.getContent())
                .relatedHeritage(explorationCreateDto.getRelatedHeritage())
                .member(Member.builder()
                        .id(explorationCreateDto.getMemberId())
                        .build())
                .build();
        Exploration resultExploration = explorationRepository.save(exploration);
        // 1. 문화재 탐방기 이미지 파일 저장
        if(imageFileList != null && !imageFileList.isEmpty()){
            for(MultipartFile imageFile : imageFileList) {
                String originalFileName = imageFile.getOriginalFilename();
                String storedFileName = createStoredFileName(originalFileName);
                String filePath = uploadDirPath + storedFileName;
                // 실제 파일 저장
                imageFile.transferTo(new File(filePath));
                // 2. 문화재 탐방기 이미지 DB 저장
                ExplorationPhoto explorationPhoto = ExplorationPhoto.builder()
                        .originalName(originalFileName)
                        .savedName(storedFileName)
                        .fileExtension(getFileExt(originalFileName))
                        .path(filePath)
                        .size(imageFile.getSize())
                        .build();
                exploration.addPhoto(explorationPhoto);
                explorationPhotoRepository.save(explorationPhoto);
            }
        }
        return ExplorationDetailDto.from(resultExploration);
    }

    // UUID와 확장자를 포함한 파일 이름 생성 메서드
    private String createStoredFileName(String originalFileName) {
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + getFileExt(originalFileName);
    }

    private String getFileExt(String originalFileName){
        String ext = originalFileName.substring(originalFileName.lastIndexOf(".") + 1);
        return ext;
    }

    public ExplorationDetailDto viewExploration(Long id) {
        Exploration exploration = explorationRepository.findById(id)
                .orElseThrow(()-> new RuntimeException(("게시글이 존재하지 않습니다!")));
        ExplorationDetailDto explorationDetailDto = ExplorationDetailDto.from(exploration);
        explorationDetailDto.setBookmarkCount(explorationBookmarkRepository.countByExplorationId(id));
        explorationDetailDto.setCommentCount(explorationCommentRepository.countByExplorationId(id));
        return explorationDetailDto;
    }

    @Transactional
    public void deleteExploration(Long id, Long memberId){
        Exploration exploration = explorationRepository.findById(id).orElseThrow(() -> {
            throw new RuntimeException("게시글이 존재하지 않습니다");
        });
        if(exploration.getMember().getId() != memberId)
            throw new IllegalArgumentException("본인이 작성한 글만 삭제할 수 있습니다.");
        explorationRepository.deleteById(id);
    }

    @Transactional
    public ExplorationDetailDto editExploration(ExplorationUpdateDto explorationUpdateDto, List<MultipartFile> imageFileList, Long memberId) throws IOException {
        // Exploration 게시물 저장 폴더 확인
        File uploadDir = new File(uploadDirPath);
        if(!uploadDir.exists()){
            uploadDir.mkdirs();
        }

        // 게시물 조회
        Exploration updateExploration = explorationRepository.findById(explorationUpdateDto.getId()).orElseThrow(()->{
            throw new RuntimeException("해당하는 문화재 탐방기를 찾지 못했습니다");
        });

        // 텍스트 정보 업데이트
        updateExploration.update(explorationUpdateDto);

        // 이미지 삭제
         List<String> pathsToDelete = explorationUpdateDto.getImagesToDelete();
         if(pathsToDelete != null && !pathsToDelete.isEmpty()){
             updateExploration.getExplorationPhotos().removeIf(photo ->{
                 boolean shouldDelete = pathsToDelete.contains(photo.getSavedName());
                 if(shouldDelete){
                     new File(photo.getPath()).delete();
                 }
                 return shouldDelete;
             });
         }

         // 이미지 추가
        if(imageFileList != null && !imageFileList.isEmpty()){
            for(MultipartFile image : imageFileList){
                String originalFileName = image.getOriginalFilename();
                String storedFileName = createStoredFileName(originalFileName);
                String filePath = uploadDirPath + storedFileName;
                // 실제 파일 저장
                image.transferTo(new File(filePath));
                ExplorationPhoto explorationPhoto = ExplorationPhoto.builder()
                        .originalName(originalFileName)
                        .savedName(storedFileName)
                        .fileExtension(getFileExt(originalFileName))
                        .path(filePath)
                        .size(image.getSize())
                        .build();
                updateExploration.addPhoto(explorationPhoto);
                explorationPhotoRepository.save(explorationPhoto);
            }
        }
        return ExplorationDetailDto.from(updateExploration);
    }

    public List<ExplorationDetailDto> getExplorationList() {
        // Exploration과 bookmarkCount, commentCount 정보를 닮은 dtoList 먼저 반환
        List<ExplorationDetailDto> dtoList = explorationRepository.findAllWithBookmarkCountAndCommentCounts();
        // 조회 결과가 없으면 바로 반환
        if (dtoList.isEmpty()) {
            return dtoList;
        }
        // 전체 문화재 탐방기 이미지 리스트 반환
        List<Exploration> explorationsWithPhotos = explorationRepository.findAllWithExplorationPhotos();
        Map<Long, List<String>> photoMap = explorationsWithPhotos.stream()
                .collect(Collectors.toMap(
                        Exploration::getId, // Key: Exploration ID
                        e -> e.getExplorationPhotos().stream() // Value: 이미지 파일명 리스트
                                .map(ExplorationPhoto::getSavedName)
                                .toList()
                ));

        // 최종 조합: 기본 DTO 리스트에 이미지 정보 주입(Hydration)
        dtoList.forEach(dto -> {
            List<String> photoNames = photoMap.get(dto.getId());
            if (photoNames != null) {
                dto.setImageNameList(photoNames);
            }
        });
        return dtoList;
    }

    @Transactional
    public void createExplorationBookmark(ExplorationBookmarkRequestDto explorationBookmarkRequestDto) {
        explorationBookmarkRepository.save(explorationBookmarkRequestDto.toExplorationBookmark());
    }

    @Transactional
    public void deleteExplorationBookmark(ExplorationBookmarkRequestDto explorationBookmarkRequestDto) {
        explorationBookmarkRepository.deleteByMemberIdAndExplorationId(explorationBookmarkRequestDto.getMemberId(), explorationBookmarkRequestDto.getExplorationId());
    }

    public Boolean checkExplorationBookmarked(Long memberId, Long explorationId) {
        Boolean checker = explorationBookmarkRepository.existsByMemberIdAndExplorationId(memberId,explorationId);
        log.debug("{}", checker);
        return checker;
    }

    public List<ExplorationDetailDto> searchExploration(String keyword) {
        List<Exploration> explorationList = explorationRepository.findByKeyword(keyword);
        List<ExplorationDetailDto> explorationDetailDtoList = explorationList.stream().map(exploration -> ExplorationDetailDto.from(exploration)).toList().reversed();
        return explorationDetailDtoList;
    }

    @Transactional
    public ReportResponseDto createExplorationReport(ReportCreateRequestDto explorationReportCreateRequestDto) {
        Report newExplorationReport = Report.builder()
                .contentType(ContentType.EXPLORATION)
                .contentId(explorationRepository.findById(explorationReportCreateRequestDto.getPostId())
                        .orElseThrow(()->{
                            throw new RuntimeException("explorationId에 해당하는 member가 존재하지 않습니다");
                        }).getId())
                .reason(explorationReportCreateRequestDto.getReason())
                .reportMember(memberRepository.findById(explorationReportCreateRequestDto.getMemberId())
                        .orElseThrow(() -> {
                            throw new RuntimeException("memberId에 해당하는 exploration이 존재하지 않습니다");
                        }))
                .build();
        Report resultExplorationReport = reportRepository.save(newExplorationReport);
        return ReportResponseDto.from(resultExplorationReport);
    }
}
