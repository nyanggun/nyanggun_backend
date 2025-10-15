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
public class EncyclopediaBookmarkDto {

    private Member member;
    private HeritageEncyclopedia heritageEncyclopedia;

    public static EncyclopediaBookmarkDto from(EncyclopediaBookmark bookmark){
        return EncyclopediaBookmarkDto.builder()
                .member(bookmark.getMember())
                .heritageEncyclopedia(bookmark.getHeritageEncyclopedia())
                .build();
    }
}
