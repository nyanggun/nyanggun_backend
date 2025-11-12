package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.*;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.ProfilePicture;
import org.kosa.congmouse.nyanggoon.entity.Talk;
import org.kosa.congmouse.nyanggoon.entity.TalkPicture;
import org.kosa.congmouse.nyanggoon.repository.*;

import org.kosa.congmouse.nyanggoon.security.jwt.JwtUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MemberRepository memberRepository;
    private final ProfilePictureRepository profilePictureRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;  // JsonLoginFilter에서 쓰던 JWT 유틸
    private final PhotoBoxRepository photoBoxRepository;
    private final PostRepository postRepository;
    private final TalkPictureRepository talkPictureRepository;
    private final ExplorationPhotoRepository explorationPhotoRepository;
    private final CommentRepository commentRepository;



    //유저의 정보를 확인하는 메소드 입니다.

    public MemberResponseDto getMemberInfo(Long id){

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원이 존재하지 않습니다. id=" + id));

        ProfilePicture profilePicture = profilePictureRepository.findById(id).orElse(null);

        MemberResponseDto memberResponseDto = MemberResponseDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .profileImagePath(profilePicture != null ? profilePicture.getPath() : null)
                .phoneNumber(member.getPhoneNumber())
                .build();


        return memberResponseDto;
    }

    //유저의 정보를 수정하는 메소드 입니다.

    @Transactional
    public TokenResponse updateUserInfo(Long id, MemberUpdateRequestDto memberUpdateRequestDto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));

        if (!member.getId().equals(id)) {
            throw new AccessDeniedException("회원 정보 수정 권한이 없습니다.");
        }

        if (!passwordEncoder.matches(memberUpdateRequestDto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다.");
        }
        // DTO 기반 새 객체 생성
        member.updateInfo(
                memberUpdateRequestDto.getEmail(),
                memberUpdateRequestDto.getNickname(),
                memberUpdateRequestDto.getPhoneNumber()
        );

        memberRepository.save(member);

        // 5. 새 JWT 발급 (기존 JsonLoginFilter와 동일 방식)
        long expiredMs = 1000L * 60 * 60 * 24; // 1일
        String newToken = jwtUtil.createJwt(member, expiredMs);

        log.info("회원 정보 수정 완료 및 새 토큰 발급: {}", member.getEmail());

        // 6. 새 토큰 반환
        return new TokenResponse(newToken);

    }

    //회원 탈퇴를 하는 메소드 입니다.
    @Transactional
    public void deleteUserInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
        //SecurityContext 에서 현재 인증된 사용자 정보를 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 username 추출 (username = 이메일)
        String username = authentication.getName();

        // 권한 체크
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
        log.info("username from auth: {}", username);
        log.info("member email: {}", member.getEmail());

        // 본인 또는 관리자면 가능
        if (!member.getEmail().equals(username) && !isAdmin) {
            throw new AccessDeniedException("회원 탈퇴 권한이 없습니다.");
        }

        memberRepository.delete(member);
        log.info("회원 탈퇴 완료: {}", member.getEmail());
    }


    //회원이 작성한 사진함을 가져오는 메소드 입니다.
    public PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> getPhotoBoxListById(Long id, Long cursorId){
        int pageSize = 10;
        List<PhotoBoxSummaryResponseDto> photoBoxContents;

        if (cursorId == null) {
            photoBoxContents = photoBoxRepository.findPhotoBoxById(id, PageRequest.of(0, pageSize));
        } else {
            photoBoxContents = photoBoxRepository.findPhotoBoxNextById(id, cursorId,  PageRequest.of(0, pageSize));
        }

        Long nextCursor = photoBoxContents.isEmpty() ? null : photoBoxContents.get(photoBoxContents.size() - 1).getPhotoBoxId() - 1;
        boolean hasNext = photoBoxContents.size() == pageSize;

        return new PhotoBoxCursorResponseDto<>(photoBoxContents, nextCursor, hasNext);
    }

    //회원이 북마크한 사진함을 가져오는 메소드 입니다.
    public PhotoBoxCursorResponseDto<List<PhotoBoxSummaryResponseDto>> getPhotoBoxBookmarkListById(Long id, Long cursorId) {

        int pageSize = 10;
        List<PhotoBoxSummaryResponseDto> photoBoxContents;

        if (cursorId == null) {
            photoBoxContents = photoBoxRepository.findPhotoBoxBookmarkById(id, PageRequest.of(0, pageSize));
        } else {
            photoBoxContents = photoBoxRepository.findPhotoBoxBookmarkNextById(id, cursorId,  PageRequest.of(0, pageSize));
        }

        Long nextCursor = photoBoxContents.isEmpty() ? null : photoBoxContents.get(photoBoxContents.size() - 1).getPhotoBoxId() - 1;
        boolean hasNext = photoBoxContents.size() == pageSize;

        return new PhotoBoxCursorResponseDto<>(photoBoxContents, nextCursor, hasNext);
    }

        //회원이 작성한 게시글들을 가져오는 메소드 입니다.
        public PostCursorResponseDto findAllPostsById(Long userId, Long cursor) {

            log.info("작성 게시글 가져오는 중");
            int pageSize = 3;

            // 게시글 조회 (cursor 기반)
            List<Object[]> results;
            if (cursor == null) {
                results = postRepository.getUserPostList(userId, pageSize);
            } else {
                results = postRepository.getUserPostListNext(userId, cursor, pageSize);
            }



            List<PostListSummaryResponseDto> postList = results.stream().map(obj -> {
                Long postId = ((Number) obj[0]).longValue();
                String title = (String) obj[1];
                String content = (String) obj[2];
                LocalDateTime createdAt = obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null;
                Long memberId = ((Number) obj[4]).longValue();
                String nickname = (String) obj[5];
                String relatedHeritage = (String) obj[6];
                String category = (String) obj[7];

                List<TalkPictureResponseDto> talkPictures = null;
                List<ExplorationPictureResponseDto> explorationPictures = null;

                if ("TALK".equals(category)) {
                    talkPictures = talkPictureRepository.findByTalkId(postId)
                            .stream()
                            .map(TalkPictureResponseDto::from)
                            .toList();
                } else if ("EXPLORATION".equals(category)) {
                    explorationPictures = explorationPhotoRepository.findByExplorationIdOrderByIdAsc(postId)
                            .stream()
                            .map(ExplorationPictureResponseDto::from)
                            .toList();
                }

                return PostListSummaryResponseDto.builder()
                        .postId(postId)
                        .title(title)
                        .content(content)
                        .createdAt(createdAt)
                        .memberId(memberId)
                        .category(category)
                        .nickname(nickname)
                        .relatedHeritage(relatedHeritage)
                        .talkPictureList(talkPictures)
                        .explorationPictureList(explorationPictures)
                        .build();
            }).toList();


            // 커서 계산
            Long nextCursor = postList.isEmpty() ? null : postList.get(postList.size() - 1).getPostId();
            boolean hasNext = postList.size() == pageSize;
            log.info("작성 게시글 가져오기 완료 ");
            return new PostCursorResponseDto(postList, nextCursor, hasNext);

        }
    //회원이 북마크한 게시글들을 가져오는 메소드 입니다.
    public PostCursorResponseDto<List<PostListSummaryResponseDto>> findBookmarkPostsById(Long userId, Long cursor) {


        log.info("북마크한 게시글 가져오는 중");
        int pageSize = 3;

        // 게시글 조회 (cursor 기반)
        List<Object[]> results;
        if (cursor == null) {
            results = postRepository.getUserBookmarkList(userId, pageSize);
        } else {
            results = postRepository.getUserBookmarkListNext(userId, cursor, pageSize);
        }



        List<PostListSummaryResponseDto> postList = results.stream().map(obj -> {
            Long postId = ((Number) obj[0]).longValue();
            String title = (String) obj[1];
            String content = (String) obj[2];
            LocalDateTime createdAt = obj[3] != null ? ((Timestamp) obj[3]).toLocalDateTime() : null;
            Long memberId = ((Number) obj[4]).longValue();
            String nickname = (String) obj[5];
            String relatedHeritage = (String) obj[6];
            String category = (String) obj[7];

            List<TalkPictureResponseDto> talkPictures = null;
            List<ExplorationPictureResponseDto> explorationPictures = null;

            if ("TALK".equals(category)) {
                talkPictures = talkPictureRepository.findByTalkId(postId)
                        .stream()
                        .map(TalkPictureResponseDto::from)
                        .toList();
            } else if ("EXPLORATION".equals(category)) {
                explorationPictures = explorationPhotoRepository.findByExplorationIdOrderByIdAsc(postId)
                        .stream()
                        .map(ExplorationPictureResponseDto::from)
                        .toList();
            }

            return PostListSummaryResponseDto.builder()
                    .postId(postId)
                    .title(title)
                    .content(content)
                    .createdAt(createdAt)
                    .memberId(memberId)
                    .category(category)
                    .nickname(nickname)
                    .relatedHeritage(relatedHeritage)
                    .talkPictureList(talkPictures)
                    .explorationPictureList(explorationPictures)
                    .build();
        }).toList();


        // 커서 계산
        Long nextCursor = postList.isEmpty() ? null : postList.get(postList.size() - 1).getPostId();
        boolean hasNext = postList.size() == pageSize;
        log.info("북마크한 게시글 가져오기 완료 ");
        return new PostCursorResponseDto(postList, nextCursor, hasNext);


    }
    //회원이 작성한 댓글들을 가져오는 메소드 입니다.
    public CommentCursorResponseDto<List<CommentResponseDto>> findCommentById(Long userId, Long cursor) {
        log.info("북마크한 게시글 가져오는 중");
        int pageSize = 3;

        // 댓글 조회 (cursor 기반)
        List<Object[]> results;
        if (cursor == null) {
            results = commentRepository.getUserComments(userId, pageSize);
        } else {
            results = commentRepository.getUserCommentsNext(userId, cursor, pageSize);
        }
        
        List<CommentResponseDto> commentList = results.stream().map(obj -> {
            Long commentId = ((Number) obj[0]).longValue();                     // 첫 컬럼: comment_id
            String content = (String) obj[1];                                   // 두번째 컬럼: content
            LocalDateTime createdAt = obj[2] != null ? ((Timestamp) obj[2]).toLocalDateTime() : null; // created_at
            Long memberId = ((Number) obj[3]).longValue();                       // member_id
            String nickname = (String) obj[4];              // nickname (새로 추가됨)
            Long postId = ((Number) obj[5]).longValue();    // post_id
            String category = (String) obj[6];                                // "EXPLORATION" or "TALK"

            return CommentResponseDto.builder()
                    .commentId(commentId)
                    .content(content)
                    .createdAt(createdAt)
                    .memberId(memberId)
                    .nickname(nickname)
                    .postId(postId)
                    .category(category)
                    .build();
        }).toList();


        // 커서 계산
        Long nextCursor = commentList.isEmpty() ? null : commentList.get(commentList.size() - 1).getCommentId();
        boolean hasNext = commentList.size() == pageSize;
        log.info("작성한 댓글 가져오기 완료 ");
        return new CommentCursorResponseDto(commentList, nextCursor, hasNext);



    }
}