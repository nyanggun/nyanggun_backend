package org.kosa.congmouse.nyanggoon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ExplorationCommentCreateDto {

    @Schema(description = "문화재 탐방기 댓글 내용", example="댓글 내용입니다!")
    private String content;

    @Schema(description = "멤버 id", example="1")
    private Long memberId;

    @Schema(description = "문화재 탐방기 id", example="1")
    private Long explorationId;

    @Schema(description = "부모 댓글 id(대댓글 작성시 기입)", example="1")
    private Long parentExplorationCommentId;
}
