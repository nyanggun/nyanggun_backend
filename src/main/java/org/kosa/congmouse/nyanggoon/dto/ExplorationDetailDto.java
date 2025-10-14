package org.kosa.congmouse.nyanggoon.dto;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.ExplorationPhoto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.entity.MemberSimpleResponseDto;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ExplorationDetailDto {
    private Long id;
    private LocalDateTime createdAt;
    private String title;
    private String content;
    private String relatedHeritage;
    private MemberSimpleResponseDto member;
    private Long bookmarkCount;
    private Long commentCount;
    private List<String> imagePath;

    public static ExplorationDetailDto from(Exploration exploration) {
        ExplorationDetailDto explorationDetailDto = ExplorationDetailDto.builder()
                .id(exploration.getId())
                .createdAt(exploration.getCreatedAt())
                .title(exploration.getTitle())
                .content(exploration.getContent())
                .relatedHeritage(exploration.getRelatedHeritage())
                .member(MemberSimpleResponseDto.builder()
                        .id(exploration.getMember().getId())
                        .nickname(exploration.getMember().getNickname())
                        .build())
                .imagePath(exploration.getExplorationPhotos().stream().map(ExplorationPhoto::getPath).toList())
                .build();
        return explorationDetailDto;
    }
}
