package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.Member;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncyclopediaBookmarkResponseDto {
    private Long id;
    private Member member;
    private HeritageEncyclopedia heritageEncyclopedia;

    public static EncyclopediaBookmark toEntity(EncyclopediaBookmarkResponseDto bookmarkDto){
        Member member = bookmarkDto.getMember();
        HeritageEncyclopedia heritageEncyclopedia = bookmarkDto.getHeritageEncyclopedia();
        return EncyclopediaBookmark.builder()
                .member(member)
                .heritageEncyclopedia(heritageEncyclopedia)
                .build();
    }
}
