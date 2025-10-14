package org.kosa.congmouse.nyanggoon.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class HeritageEncyclopediaResponseDto {
    private Long id;
    private int subjectCode;
    private String manageNumber;
    private String name;
    private String chineseName;
    private int cityCode;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String heritageCode;
    private String address;
    private String period;
    private String imageUrl;
    private String content;

    public static HeritageEncyclopediaResponseDto from(HeritageEncyclopedia HeritageEncyclopedia){
        return HeritageEncyclopediaResponseDto.builder()
                .id(HeritageEncyclopedia.getId())
                .subjectCode(HeritageEncyclopedia.getSubjectCode())
                .manageNumber(HeritageEncyclopedia.getManageNumber())
                .name(HeritageEncyclopedia.getName())
                .chineseName(HeritageEncyclopedia.getChineseName())
                .cityCode(HeritageEncyclopedia.getCityCode())
                .longitude(HeritageEncyclopedia.getLongitude())
                .latitude(HeritageEncyclopedia.getLatitude())
                .heritageCode(HeritageEncyclopedia.getHeritageCode())
                .address(HeritageEncyclopedia.getAddress())
                .period(HeritageEncyclopedia.getPeriod())
                .imageUrl(HeritageEncyclopedia.getImageUrl())
                .content(HeritageEncyclopedia.getContent())
                .build();
    }
}
