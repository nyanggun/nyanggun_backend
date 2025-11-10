package org.kosa.congmouse.nyanggoon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportCreateRequestDto {

    @Schema(description = "신고 사유", example="이래서 저래서 신고했습니다!")
    private String reason;

    @Schema(description = "게시물/댓글 id", example="1")
    private Long postId;

    @Schema(description = "신고자 id", example="1")
    private Long memberId;
}
