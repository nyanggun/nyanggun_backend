package org.kosa.congmouse.nyanggoon.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberSimpleResponseDto {
    private Long id;
    private String nickname;
}
