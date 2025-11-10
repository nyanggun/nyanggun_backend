package org.kosa.congmouse.nyanggoon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.ExplorationBookmark;
import org.kosa.congmouse.nyanggoon.entity.Member;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplorationBookmarkRequestDto {

    @Schema(description = "문화재 탐방기 북마크 id", example="1")
    private Long id;

    @Schema(description = "멤버 id", example="1")
    private Long memberId;

    @Schema(description = "문화재 탐방기 id", example="1")
    private Long explorationId;

    public static ExplorationBookmarkRequestDto from(ExplorationBookmark explorationBookmark) {
        return ExplorationBookmarkRequestDto.builder()
                .id(explorationBookmark.getId())
                .memberId(explorationBookmark.getMember().getId())
                .explorationId(explorationBookmark.getExploration().getId())
                .build();
    }

    public ExplorationBookmark toExplorationBookmark(){
        return ExplorationBookmark.builder()
                .member(Member.builder().id(memberId).build())
                .exploration(Exploration.builder().id(explorationId).build())
                .build();
    }
}
