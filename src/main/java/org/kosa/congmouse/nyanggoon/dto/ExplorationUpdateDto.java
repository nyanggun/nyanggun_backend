package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExplorationUpdateDto {
    private Long id;
    private String title;
    private String content;
    private String relatedHeritage;
    private Long memberId;
    private List<String> imagesToDelete;
}
