package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Talk;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//담소 게시물을 상세 확인할 수 있게 하는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkDetailResponseDto {

    private Long talkId;
    private String title;
    private String content;
    private Long memberId;
    private String nickname;
    private LocalDateTime createdAt;
    private List<TalkPictureResponseDto> talkPictureList; //게시글 사진들 추가
    private long commentCount;   // 댓글 수
    private long bookmarkCount;  // 북마크 수
    private boolean isBookmarked; // 북마크 여부
    // 댓글 리스트 추가
    private List<TalkCommentResponseDto> comments;

    public static TalkDetailResponseDto from(Talk talk , List<TalkCommentResponseDto> comments, Long commentCount, Long bookmarkCount,  boolean isBookmarked){
        return TalkDetailResponseDto.builder()
                .talkId(talk.getId())
                .title(talk.getTitle())
                .content(talk.getContent())
                .memberId(talk.getMember().getId())
                .nickname(talk.getMember().getNickname())
                .createdAt(talk.getCreatedAt())
                .talkPictureList(
                        talk.getTalkPictures() != null
                                ? talk.getTalkPictures().stream()
                                .map(TalkPictureResponseDto::from)
                                .toList()
                                : List.of()
                )
                .comments(comments)
                .commentCount(commentCount)
                .bookmarkCount(bookmarkCount)
                .isBookmarked(isBookmarked)
                .build();
    }
}
