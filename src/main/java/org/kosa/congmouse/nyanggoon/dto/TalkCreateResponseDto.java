package org.kosa.congmouse.nyanggoon.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Member;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkCreateResponseDto {
    private Long talkId;
    private String title;
    private String content;
    private Member member;
    private LocalDateTime createdAt;
    private List<TalkPictureResponseDto> talkPictureList;

}
