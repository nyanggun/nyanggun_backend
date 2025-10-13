package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//사진함을 수정하는 Dto 입니다.
public class PhotoBoxUpdateRequestDto {

    private String title;
    private String relatedHeritage;
    private Long memberid;
    private List<String> tags;
}

