package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Talk;

import java.time.LocalDateTime;

//담소 게시글 목록을 불러오는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkListSummaryResponseDto {
    private Long talkId;
    private String title;
    private String content;
    private String authorName;
    private LocalDateTime createdAt;

    public static TalkListSummaryResponseDto from(Talk talk){
        return TalkListSummaryResponseDto.builder()
                .talkId(talk.getId())
                .title(talk.getTitle())
                .content(talk.getContent())
                .authorName(talk.getMember().getNickname())
                .createdAt(talk.getCreatedAt())
                .build();
    }

}
