package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.ContentState;
import org.kosa.congmouse.nyanggoon.entity.TalkComment;

import java.time.LocalDateTime;

//담소 댓글을 가져오는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkCommentResponseDto {

    private Long talkCommentId;
    private String content;
    private LocalDateTime createdAt;
    private Long memberId;
    private String nickname;
    private Long talkId;
    private ContentState contentState;
    private Long talkParentCommentId;

    public static TalkCommentResponseDto from(TalkComment talkComment){
        return TalkCommentResponseDto.builder()
                .talkCommentId(talkComment.getId())
                .content(talkComment.getContent())
                .createdAt(talkComment.getCreatedAt())
                .memberId(talkComment.getMember().getId())
                .contentState(talkComment.getContentState())
                .nickname(talkComment.getMember().getNickname())
                .talkId(talkComment.getTalk().getId())
                //null 여부 체크
                .talkParentCommentId(talkComment.getParentComment() != null ? talkComment.getParentComment().getId() : null)
                .build();

    }

}
