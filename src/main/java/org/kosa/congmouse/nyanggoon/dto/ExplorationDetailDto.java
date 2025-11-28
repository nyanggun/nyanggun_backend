package org.kosa.congmouse.nyanggoon.dto;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.entity.Exploration;
import org.kosa.congmouse.nyanggoon.entity.ExplorationPhoto;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.dto.MemberSimpleResponseDto;

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
    private List<String> imageNameList;

    public ExplorationDetailDto(Long id, LocalDateTime createdAt, String title, String content, String relatedHeritage,
                                Long memberId, String memberNickname, Long bookmarkCount, Long commentCount) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.content = content;
        this.relatedHeritage = relatedHeritage;
        // memberId와 memberNickname으로 MemberSimpleResponseDto 객체를 직접 만들어줍니다.
        this.member = new MemberSimpleResponseDto(memberId, memberNickname);
        this.bookmarkCount = bookmarkCount;
        this.commentCount = commentCount;
        // imageNameList는 이 쿼리로 가져오지 않으므로, null이나 빈 리스트로 초기화합니다.
        this.imageNameList = new java.util.ArrayList<>();
    }

    public static ExplorationDetailDto from(Exploration exploration, List<ExplorationPhoto> explorationPhotoList) {
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
                .imageNameList(explorationPhotoList
                        .stream()
                        .map(explorationPhoto -> explorationPhoto.getSavedName())
                        .toList())
                .build();
        return explorationDetailDto;
    }
}
