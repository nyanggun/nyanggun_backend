package org.kosa.congmouse.nyanggoon.dto;

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
    private Long id;
    private Long memberId;
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
