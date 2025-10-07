package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//댓글을 작성하는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkCommentCreateRequestDto {
    private Long commentId;
    private Long talkId;
    private Long memberId;
    private String content;
    private Long parentCommentId;

}
