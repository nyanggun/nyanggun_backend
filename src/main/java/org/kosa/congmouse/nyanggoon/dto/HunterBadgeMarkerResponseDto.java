package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;
import org.kosa.congmouse.nyanggoon.entity.HunterBadge;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HunterBadgeMarkerResponseDto {
    private Long badgeId;
    private String name;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String imgUrl;
    private Long heritageEncyclopediaId;

    public static HunterBadgeMarkerResponseDto from(HunterBadge hunterBadge){
        return HunterBadgeMarkerResponseDto.builder()
                .badgeId(hunterBadge.getId())
                .name(hunterBadge.getName())
                .longitude(hunterBadge.getLongitude())
                .latitude(hunterBadge.getLatitude())
                .imgUrl(hunterBadge.getImgUrl())
                .heritageEncyclopediaId(hunterBadge.getHeritageEncyclopedia().getId())
                .build();
    }
}
