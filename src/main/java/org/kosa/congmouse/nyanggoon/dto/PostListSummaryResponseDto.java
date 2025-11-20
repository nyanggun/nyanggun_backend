package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.Talk;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostListSummaryResponseDto {
    private Long postId;
    private String category;
    private String title;
    private String content;
    private Long memberId;
    private String nickname;
    private LocalDateTime createdAt;


    private List<TalkPictureResponseDto> talkPictureList; //게시글 사진들 추가

    private List<ExplorationPictureResponseDto> explorationPictureList; //게시글 사진들 추가
    private String relatedHeritage;


    private boolean isBookmarked;
    private Long commentCount; // 댓글 개수 추가
    private Long bookmarkCount; //북마크 개수 추가

//    public static PostListSummaryResponseDto fromTalk(Talk talk, List<TalkPictureResponseDto> talkPictureList){
//        return PostListSummaryResponseDto.builder()
//                .talkId(talk.getId())
//                .title(talk.getTitle())
//                .content(talk.getContent())
//                .memberId(talk.getMember().getId())
//                .nickname(talk.getMember().getNickname())
//                .talkPictureList(talkPictureList)
//                .createdAt(talk.getCreatedAt())
//                .build();
//    }
//    public static PostListSummaryResponseDto fromExploration(Exploration exploration, List<ExplorationPictureResponseDto> explorationPictureList){
//        return PostListSummaryResponseDto.builder()
//                .talkId(exploration.getId())
//                .title(exploration.getTitle())
//                .content(exploration.getContent())
//                .relatedHeritage(exploration.getRelatedHeritage())
//                .memberId(exploration.getMember().getId())
//                .nickname(exploration.getMember().getNickname())
//                .explorationPictureList(explorationPictureList)
//                .createdAt(exploration.getCreatedAt())
//                .build();
//    }
}
