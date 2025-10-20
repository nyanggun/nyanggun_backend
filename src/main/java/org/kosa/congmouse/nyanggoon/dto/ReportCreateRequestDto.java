package org.kosa.congmouse.nyanggoon.dto;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReportCreateRequestDto {
    private String reason;
    private Long postId;
    private Long memberId;
}
