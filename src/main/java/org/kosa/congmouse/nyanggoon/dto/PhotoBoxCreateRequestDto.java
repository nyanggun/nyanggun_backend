package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

//사진함을 작성하는 Dto 입니다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhotoBoxCreateRequestDto {

    private String title;
    private String relatedHeritage;
    private Long memberid;
    private List<String> tags;


}
