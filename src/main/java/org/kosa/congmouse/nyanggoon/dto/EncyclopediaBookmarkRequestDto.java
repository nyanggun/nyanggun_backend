package org.kosa.congmouse.nyanggoon.dto;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncyclopediaBookmarkRequestDto {
    private Member member;
    private HeritageEncyclopedia heritageEncyclopedia;

    public static EncyclopediaBookmarkRequestDto from(EncyclopediaBookmark bookmark){
        return EncyclopediaBookmarkRequestDto.builder()
                .member(bookmark.getMember())
                .heritageEncyclopedia(bookmark.getHeritageEncyclopedia())
                .build();
    }
}
