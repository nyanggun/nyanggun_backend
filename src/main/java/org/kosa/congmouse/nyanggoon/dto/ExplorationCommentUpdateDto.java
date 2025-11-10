package org.kosa.congmouse.nyanggoon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExplorationCommentUpdateDto {

    @Schema(description = "문화재 탐방기 댓글 id", example = "1")
    private Long id;

    @Schema(description = "문화재 탐방기 댓글 내용", example = "수정할 내용입니다!")
    private String content;
}
