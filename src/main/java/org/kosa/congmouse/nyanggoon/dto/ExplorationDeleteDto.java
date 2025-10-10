package org.kosa.congmouse.nyanggoon.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ExplorationDeleteDto {
    private Long id;
    private Long memberId;
}
