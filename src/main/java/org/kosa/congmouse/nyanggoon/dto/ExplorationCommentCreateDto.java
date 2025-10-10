package org.kosa.congmouse.nyanggoon.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExplorationCommentCreateDto {

    private String content;
    private Long memberId;
    private Long explorationId;
    private Long parentExplorationCommentId;
}
