package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.ExplorationPhoto;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplorationPictureResponseDto {
    private Long explorationId;
    private Long explorationPictureId;
    private String path;

    public static ExplorationPictureResponseDto from(ExplorationPhoto explorationPhoto) {
        return ExplorationPictureResponseDto.builder()
                .explorationId(explorationPhoto.getExploration().getId())
                .explorationPictureId(explorationPhoto.getId())
                .path(explorationPhoto.getPath())
                .build();
    }


}
