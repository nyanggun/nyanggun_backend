package org.kosa.congmouse.nyanggoon.dto;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.Member;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExplorationDetailDto {
    private Long id;
    private LocalDateTime createdAt;
    private String content;
    private String relatedHeritage;
    private Long memberId;

    public static ExplorationDetailDto from(Exploration exploration) {
        ExplorationDetailDto explorationDetailDto = ExplorationDetailDto.builder()
                .id(exploration.getId())
                .createdAt(exploration.getCreatedAt())
                .content(exploration.getContent())
                .relatedHeritage(exploration.getRelatedHeritage())
                .memberId(exploration.getMember().getId())
                .build();
        return explorationDetailDto;
    }
}
