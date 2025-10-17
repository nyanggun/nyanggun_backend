package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//담소 게시글을 수정할 때 사용하는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkUpdateRequestDto {
    private String title;
    private String content;
    private Long talkId;
    private List<Long> remainingImages;
}
