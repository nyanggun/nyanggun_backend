package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.TalkPicture;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TalkPictureResponseDto {
    private Long talkId;
    private Long talkPictureId;
    private String path;

    public static TalkPictureResponseDto from(TalkPicture talkPicture) {
        return TalkPictureResponseDto.builder()
                .talkId(talkPicture.getTalk().getId())
                .talkPictureId(talkPicture.getId())
                .path(talkPicture.getPath())
                .build();
    }
}
