package org.kosa.congmouse.nyanggoon.service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.*;
import org.kosa.congmouse.nyanggoon.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly= true)
@RequiredArgsConstructor
@Slf4j
//담소 게시글의 Service 입니다.
public class TalkService {

    private final TalkRepository talkRepository;
    private final MemberRepository memberRepository;
    private final TalkCommentRepository talkCommentRepository;
    private final TalkBookmarkRepository talkBookmarkRepository;
    private final TalkPictureRepository talkPictureRepository;
    private final ReportRepository reportRepository;

    //모든 담소 게시글을 불러오는 메소드 입니다.
    public TalkCursorResponseDto<List<TalkCreateResponseDto>> findAllTalkList(String username, Long cursor) {
        int pageSize = 10;
        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용

        log.info("담소 게시물을 전부 출력합니다.");

        List<Talk> talks ;
        List<Object[]> commentCounts;
        List<Object[]> bookmarkCounts;
        List<Long> bookmarkedTalkIds;
        List<TalkListSummaryResponseDto> talkListSummaryResponseDto;
        Set<Long> bookmarkedTalkIdSet = new HashSet<>();

        //Talk 목록만 가져온 후, 해당 게시글의 북마크 여부와 댓글 개수는 따로 가져온다.
        if (cursor == null) {
            talks = talkRepository.getTalkList(PageRequest.of(0, pageSize)); // 게시글 목록
       } else {
            talks =  talkRepository.getTalkListNext(cursor, PageRequest.of(0, pageSize)); // 게시글 목록
        }

        List<Long> talkIds = talks.stream().map(Talk::getId).collect(Collectors.toList());
        commentCounts = talkRepository.countCommentsPerTalk(talkIds);
        bookmarkCounts = talkRepository.countBookmarksPerTalk(talkIds);

        // 로그인 유저 북마크 여부
        if (member != null) {
            bookmarkedTalkIds = talkBookmarkRepository.findTalkIdsByMemberWithCursor(member, talkIds);
            bookmarkedTalkIdSet.addAll(bookmarkedTalkIds);
        }

        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // talkId
                        arr -> (Long) arr[1]   // count
                ));

        Map<Long, Long> bookmarkCountMap = bookmarkCounts.stream()
                .collect(Collectors.toMap(
                        arrBook -> (Long) arrBook[0],  // talkId
                        arrBook -> (Long) arrBook[1]   // count
                ));
        talkListSummaryResponseDto = talks.stream()
                .map(t -> {
                    // 엔티티 -> DTO 변환
                    List<TalkPictureResponseDto> talkPictureResponseDto = t.getTalkPictures().stream()
                            .map(p -> TalkPictureResponseDto.builder()
                                    .talkId(p.getTalk().getId())
                                    .talkPictureId(p.getId())
                                    .path(p.getPath())
                                    .createdAt(p.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    // DTO 빌드
                    return TalkListSummaryResponseDto.builder()
                            .talkId(t.getId())
                            .title(t.getTitle())
                            .content(t.getContent())
                            .memberId(t.getMember().getId())
                            .nickname(t.getMember().getNickname())
                            .createdAt(t.getCreatedAt())
                            .talkPictureList(talkPictureResponseDto) // 변환된 DTO 리스트 사용
                            .commentCount(commentCountMap.getOrDefault(t.getId(), 0L))
                            .bookmarkCount(bookmarkCountMap.getOrDefault(t.getId(), 0L))
                            .isBookmarked(bookmarkedTalkIdSet.contains(t.getId()))
                            .build();
                })
                .collect(Collectors.toList());


        //커서 보내기
        Long nextCursor = talkListSummaryResponseDto.isEmpty() ? null : talkListSummaryResponseDto.get(talkListSummaryResponseDto.size() - 1).getTalkId() - 1;
        boolean hasNext = talkListSummaryResponseDto.size() == pageSize;

        return new TalkCursorResponseDto<>(talkListSummaryResponseDto, nextCursor, hasNext);

    }

    //담소 게시글의 상세를 조회하는 메소드 입니다.
    //댓글도 함께 가져옵니다.
    public TalkDetailResponseDto findTalkDetail(Long id, String username) {
        log.info("해당 담소 게시물의 내용을 확인합니다. {}", id);
        // 게시글 엔티티 가져오기 (사진 목록도 같이)
        Talk talk = talkRepository.findTalkDetailWithPictures(id)
                .orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
        //댓글, 북마크 개수 가져오기
        long commentCount = talkRepository.countCommentsByTalkId(id);
        long bookmarkCount = talkRepository.countBookmarksByTalkId(id);

        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용
        //북마크여부 가져오기
        boolean isBookmarked = false;
        if (member != null) {
            // 해당 게시글만 확인
            isBookmarked = talkBookmarkRepository.getBookmarkByMemberAndTalk(member.getId(), talk.getId()) != null;

        }

        //해당 게시글의 댓글들을 가져옵니다.
        List<TalkComment> talkComment = talkCommentRepository.findTalkComment(id);
        // DTO로 변환합니다.
        List<TalkCommentResponseDto> talkCommentDtos = talkComment.stream()
                .map(TalkCommentResponseDto::from)
                .toList();

        return TalkDetailResponseDto.from(talk, talkCommentDtos, commentCount, bookmarkCount, isBookmarked);
    }

    //담소 게시글을 작성하는 메소드 입니다.
    @Transactional
    public Long createTalk(TalkCreateRequestDto talkCreateRequestDto, List<MultipartFile> files, String username) {
        log.info("담소 게시글 작성 시작");

        //해당 회원이 있는 지 확인한다.
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        log.info("회원 확인 완료. 게시글 객체를 생성합니다.");

        //담소 게시글 객체를 생성한다.
        Talk talk = Talk.builder()
                .title(talkCreateRequestDto.getTitle())
                .content(talkCreateRequestDto.getContent())
                .member(member)
                .build();

        //담소 게시글을 저장한다.
        Talk savedTalk = talkRepository.save(talk);

        if (files != null) {
            //사진 저장 (여러개)
            for (MultipartFile file : files) {
                String originalName = file.getOriginalFilename(); // 사용자가 업로드한 파일 이름
                long size = file.getSize();                       // 파일 크기
                String extension = FilenameUtils.getExtension(originalName); // 확장자 추출 (commons-io)

                String filePath = null;
                if (file != null && !file.isEmpty()) {
                    filePath = saveFile(file, savedTalk.getId(), extension);
                }

                //사진 저장하는 객체
                TalkPicture talkPicture = TalkPicture.builder()
                        .talk(savedTalk)
                        .originalName(originalName)
                        .savedName(filePath)
                        .path("/uploads/talks/" + filePath)
                        .size(size)
                        .fileExtension(extension)
                        .build();

                TalkPicture saveTalkPicture = talkPictureRepository.save(talkPicture);
            }
        }

        log.info("담소 게시글 작성 완료: id={}, title={}", savedTalk.getId(), savedTalk.getTitle());

        return savedTalk.getId();

    }

    //사진을 업로드하는 메소드 입니다.
    //여러 사진을 업로드합니다. (최대 4장)
    private String saveFile(MultipartFile file, Long photoBoxId, String extension) {
        // Windows 바탕화면 경로 (현재 사용자 기준)
//        String userHome = System.getProperty("user.home"); // C:/Users/사용자명
//        String uploadDir = userHome + "/Desktop/uploads/"; // 바탕화면 하위 uploads 폴더
        // 서버 루트 기준 uploads 폴더
        String uploadDir = System.getProperty("user.dir") + "/uploads/talks/";

        //저장 파일 명 :
        String savedFileName = UUID.randomUUID() + "." + extension;

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


    //담소 게시글을 수정하는 메소드 입니다.
    @Transactional
    public Long updateTalk(Long talkId, TalkUpdateRequestDto talkUpdateRequestDto, List<MultipartFile> files, String username) {
        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        if (!talk.getMember().getEmail().equals(username)) {
            throw new AccessDeniedException("게시글 수정 권한이 없습니다.");
        }

        // 1. 제목/내용 수정
        talk.update(talkUpdateRequestDto.getTitle(), talkUpdateRequestDto.getContent());

        // 2. 기존 이미지 처리
        List<Long> remainingImageIds = talkUpdateRequestDto.getRemainingImages();
        if (talk.getTalkPictures() != null) {
            talk.getTalkPictures().removeIf(picture -> {
                // remainingImages에 포함되지 않으면 삭제
                if (remainingImageIds == null || !remainingImageIds.contains(picture.getId())) {
                    deleteFile(picture.getSavedName());
                    return true;
                }
                return false;
            });
        }

        // 3. 새 파일 업로드
        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                if (file != null && !file.isEmpty()) {
                    String extension = FilenameUtils.getExtension(file.getOriginalFilename());
                    String savedName = saveFile(file, talk.getId(), extension);
                    String path = "/uploads/talks/" + savedName;

                    TalkPicture newPicture = TalkPicture.builder()
                            .originalName(file.getOriginalFilename())
                            .savedName(savedName)
                            .path(path)
                            .size(file.getSize())
                            .fileExtension(extension)
                            .talk(talk)
                            .build();

                    talk.getTalkPictures().add(newPicture);
                }
            }
        }

        return talkId;
    }





    //담소 게시글을 삭제하는 메소드 입니다.
    @Transactional
    public void deleteTalk(Long talkId){
        log.info("해당하는 담소 게시물을 삭제합니다. {}", talkId);
       Talk talk= talkRepository.findById(talkId).orElseThrow(()-> new RuntimeException("게시글이 존재하지 않습니다."));
        //게시글 삭제하기
        if(talk.getTalkPictures() != null) {
            for(TalkPicture picture : talk.getTalkPictures()) {
                deleteFile(picture.getSavedName()); // 실제 파일 삭제
            }
        }
        //자동 지원되는 메소드를 사용한다.
        talkRepository.deleteById(talkId);
    }

    //사진 파일을 삭제하는 메소드 입니다.
    private void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        // 저장 경로 설정 (create 시 saveFile()과 동일한 경로 구조여야 함)
        Path filePath = Paths.get("uploads/talks/").resolve(fileName);

        try {
            Files.deleteIfExists(filePath); // 파일이 존재할 경우만 삭제
            log.info("파일 삭제 성공: {}", filePath);
        } catch (IOException e) {
            log.error("파일 삭제 실패: {}", filePath, e);
        }
    }



    //댓글을 작성하는 메소드 입니다.
    @Transactional
    public TalkComment createTalkComment(TalkCommentCreateRequestDto talkCommentCreateRequestDto) {
        log.info("담소 댓글 작성 시작");

        //해당 회원이 있는 지 확인한다.
        Member member = memberRepository.findById(talkCommentCreateRequestDto.getMemberId())
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다."));

        //해당 게시글이 있는 지 확인한다.
        Talk talk = talkRepository.findById(talkCommentCreateRequestDto.getTalkId()).orElseThrow(()-> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //대댓글이라면 부모 댓글을 조회한다.
        TalkComment parentComment = null;
        // 부모 댓글이 존재할 경우
        if (talkCommentCreateRequestDto.getParentCommentId() != null) {
            //현재클릭한 댓글
            TalkComment tempParent = talkCommentRepository.findById(talkCommentCreateRequestDto.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("부모 댓글이 존재하지 않습니다."));

            // 만약 지금 작성하려는 댓글의 부모가 '대댓글'이라면, (즉 대대댓글을 작성하려면)
            // 그 대댓글의 부모(즉, 최상위 댓글)를 부모로 설정
            // 즉 대대댓글이 아니라 해당 최상위 댓글의 대댓글로 들어가게됨
            if (tempParent.getParentComment() != null) {
                //지금 내가 생성하려는 댓글의 부모를 같은 부모로 둬라
                parentComment = tempParent.getParentComment();
            } else {
                //아니면 그 댓글이 부모가 된다. (일반적인 대댓글)
                parentComment = tempParent;
            }
        }
        log.info("회원 및 게시글 확인 완료. 댓글 객체를 생성합니다.");

        //담소 게시글 객체를 생성한다.
        TalkComment talkComment = TalkComment.builder()
                .content(talkCommentCreateRequestDto.getContent())
                .member(member)
                .talk(talk)
                .parentComment(parentComment) // null이면 일반 댓글
                .build();


        //담소 게시글을 저장한다.
        TalkComment savedTalkComment = talkCommentRepository.save(talkComment);
        log.info("담소 게시글 작성 완료: id={}, title={}", savedTalkComment.getId(), savedTalkComment.getContent());

        return savedTalkComment;
    }


    //댓글을 수정하는 메소드 입니다.
    @Transactional
    public void updateTalkComment(Long commentId, TalkCommentUpdateRequestDto talkCommentUpdateRequestDto) {
        log.info("담소 댓글 수정 시작");

        TalkComment talkComment= talkCommentRepository.findById(commentId).orElseThrow(()->new IllegalArgumentException("해당 댓글이 존재하지 않습니다."));
        talkComment.update(talkCommentUpdateRequestDto.getContent());
    }

    //댓글을 삭제하는 메소드 입니다.
    @Transactional
    public void deleteTalkComemnt(Long commentId) {
        log.info("댓글 삭제 시작");
        talkCommentRepository.findById(commentId);
        //자동 지원되는 메소드를 사용한다.
        talkCommentRepository.deleteById(commentId);
        log.info("댓글 삭제 완료.");
    }

    //게시글을 북마크하는 메소드 입니다.
    @Transactional
    public void createTalkBookmark(Long talkId, String username) {
        // 사용자 조회
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        // 게시글 조회
        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

        //북마크 객체 생성
        TalkBookmark talkBookmark = TalkBookmark.builder()
                .member(member)   // 현재 로그인한 사용자 엔티티
                .talk(talk)       // 북마크할 게시글 엔티티
                .build();
        //북마크 저장
        talkBookmarkRepository.save(talkBookmark);

    }

    //게시글 북마크를 취소하는 메소드 입니다.
    @Transactional
    public void deleteTalkBookmark(Long talkId, String username) {
        Member member = memberRepository.findByEmail(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Talk talk = talkRepository.findById(talkId)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 존재하지 않습니다."));

      Long bookmarkId = talkBookmarkRepository.getBookmarkWithTalkId(talkId);

       talkBookmarkRepository.deleteById(bookmarkId);

    }

    //게시글을 검색하는 메소드 입니다.
    public TalkCursorResponseDto<List<TalkCreateResponseDto>> findTalkListWithKeyword(String username, String keyword, Long cursor) {

        int pageSize = 10;

        //유저 조회
        Member member = memberRepository.findByEmail(username)
                .orElse(null); // 로그인 안 했을 수도 있으니 null 허용

        log.info("담소 게시물 검색 결과를 출력합니다.");

        List<Talk> talks ;
        List<Object[]> commentCounts;
        List<Object[]> bookmarkCounts;
        List<Long> bookmarkedTalkIds;
        List<TalkListSummaryResponseDto> talkListSummaryResponseDto;
        Set<Long> bookmarkedTalkIdSet = new HashSet<>();

        //Talk 목록만 가져온 후, 해당 게시글의 북마크 여부와 댓글 개수는 따로 가져온다.
        if (cursor == null) {
            talks = talkRepository.findTalkListWithKeyword(keyword, PageRequest.of(0, pageSize)); // 게시글 검색 목록
        } else {
            talks =  talkRepository.findTalkListWithKeywordNext(cursor, keyword, PageRequest.of(0, pageSize)); // 게시글 검색 목록
        }

        List<Long> talkIds = talks.stream().map(Talk::getId).collect(Collectors.toList());
        commentCounts = talkRepository.countCommentsPerTalk(talkIds);
        bookmarkCounts = talkRepository.countBookmarksPerTalk(talkIds);

        // 로그인 유저 북마크 여부
        if (member != null) {
            bookmarkedTalkIds = talkBookmarkRepository.findTalkIdsByMemberWithCursor(member, talkIds);
            bookmarkedTalkIdSet.addAll(bookmarkedTalkIds);
        }

        Map<Long, Long> commentCountMap = commentCounts.stream()
                .collect(Collectors.toMap(
                        arr -> (Long) arr[0],  // talkId
                        arr -> (Long) arr[1]   // count
                ));

        Map<Long, Long> bookmarkCountMap = bookmarkCounts.stream()
                .collect(Collectors.toMap(
                        arrBook -> (Long) arrBook[0],  // talkId
                        arrBook -> (Long) arrBook[1]   // count
                ));

        talkListSummaryResponseDto = talks.stream()
                .map(t -> {
                    // 엔티티 -> DTO 변환
                    List<TalkPictureResponseDto> talkPictureResponseDto = t.getTalkPictures().stream()
                            .map(p -> TalkPictureResponseDto.builder()
                                    .talkId(p.getTalk().getId())
                                    .talkPictureId(p.getId())
                                    .path(p.getPath())
                                    .createdAt(p.getCreatedAt())
                                    .build())
                            .collect(Collectors.toList());

                    // DTO 빌드
                    return TalkListSummaryResponseDto.builder()
                            .talkId(t.getId())
                            .title(t.getTitle())
                            .content(t.getContent())
                            .memberId(t.getMember().getId())
                            .nickname(t.getMember().getNickname())
                            .createdAt(t.getCreatedAt())
                            .talkPictureList(talkPictureResponseDto) // 변환된 DTO 리스트 사용
                            .commentCount(commentCountMap.getOrDefault(t.getId(), 0L))
                            .bookmarkCount(bookmarkCountMap.getOrDefault(t.getId(), 0L))
                            .isBookmarked(bookmarkedTalkIdSet.contains(t.getId()))
                            .build();
                })
                .collect(Collectors.toList());

        //커서 보내기
        Long nextCursor = talkListSummaryResponseDto.isEmpty() ? null : talkListSummaryResponseDto.get(talkListSummaryResponseDto.size() - 1).getTalkId() - 1;
        boolean hasNext = talkListSummaryResponseDto.size() == pageSize;

        return new TalkCursorResponseDto<>(talkListSummaryResponseDto, nextCursor, hasNext);

    }

    //담소 게시글을 신고하는 컨트롤러 입니다.
    @Transactional
    public ReportResponseDto createTalkReport(ReportCreateRequestDto reportCreateRequestDto) {
        Report createdTalkReport = Report.builder()
                .contentType(ContentType.TALK)
                .contentId(talkRepository.findById(reportCreateRequestDto.getPostId())
                        .orElseThrow(()->{
                            throw new RuntimeException("해당하는 문화재 담소가 존재하지 않습니다");
                        }).getId())
                .reason(reportCreateRequestDto.getReason())
                .reportMember(memberRepository.findById(reportCreateRequestDto.getMemberId())
                        .orElseThrow(()->{
                            throw new RuntimeException("해당하는 멤버가 존재하지 않습니다");
                        }))
                .build();
        reportRepository.save(createdTalkReport);
        return ReportResponseDto.from(createdTalkReport);
    }

    //담소 댓글을 신고하는 컨트롤러 입니다.
    @Transactional
    public ReportResponseDto createTalkCommentReport(ReportCreateRequestDto reportCreateRequestDto) {
        Report createdTalkCommentReport = Report.builder()
                .contentType(ContentType.TALK_COMMENT)
                .contentId(talkCommentRepository.findById(reportCreateRequestDto.getPostId())
                        .orElseThrow(()->{
                            throw new RuntimeException("해당하는 문화재 담소 댓글이 존재하지 않습니다");
                        }).getId())
                .reason(reportCreateRequestDto.getReason())
                .reportMember(memberRepository.findById(reportCreateRequestDto.getMemberId())
                        .orElseThrow(()->{
                            throw new RuntimeException("해당하는 멤버가 존재하지 않습니다");
                        }))
                .build();
        reportRepository.save(createdTalkCommentReport);
        return ReportResponseDto.from(createdTalkCommentReport);
    }
}




