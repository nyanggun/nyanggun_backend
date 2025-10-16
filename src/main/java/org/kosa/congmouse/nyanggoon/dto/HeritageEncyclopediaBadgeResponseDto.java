package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HeritageEncyclopedia;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HeritageEncyclopediaBadgeResponseDto {
    private Long id;
    private String name;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String heritageCode;

    public static HeritageEncyclopediaBadgeResponseDto from(HeritageEncyclopedia heritageEncyclopedia){
        return HeritageEncyclopediaBadgeResponseDto.builder()
                .id(heritageEncyclopedia.getId())
                .name(heritageEncyclopedia.getName())
                .longitude(heritageEncyclopedia.getLongitude())
                .latitude(heritageEncyclopedia.getLatitude())
                .build();
    }
}
