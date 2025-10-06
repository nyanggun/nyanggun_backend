package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Talk;

import java.time.LocalDateTime;

//담소 게시물을 상세 확인할 수 있게 하는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkDetailResponseDto {

    private Long talkId;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    public static TalkDetailResponseDto from(Talk talk){
        return TalkDetailResponseDto.builder()
                .talkId(talk.getId())
                .title(talk.getTitle())
                .content(talk.getContent())
                .authorName(talk.getMember().getNickname())
                .createdAt(talk.getCreatedAt())
                .build();
    }
}
