package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkPictureResponseDto {
    private Long talkId;
    private Long talkPictureId;
    private String path;
    private LocalDateTime createdAt;
}
