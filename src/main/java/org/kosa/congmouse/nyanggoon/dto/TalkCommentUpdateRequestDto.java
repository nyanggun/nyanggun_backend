package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//담소 댓글을 수정하는 Dto 입니다.
public class TalkCommentUpdateRequestDto {
    private Long commentId;
    private String content;
}
