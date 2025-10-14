package org.kosa.congmouse.nyanggoon.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.EncyclopediaBookmark;
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

    private long bookmarkCount;
    private boolean isBookmarked;

    public static HeritageEncyclopediaResponseDto from(HeritageEncyclopedia heritageEncyclopedia, long bookmarkCount, boolean isBookmarked){
        return HeritageEncyclopediaResponseDto.builder()
                .id(heritageEncyclopedia.getId())
                .subjectCode(heritageEncyclopedia.getSubjectCode())
                .manageNumber(heritageEncyclopedia.getManageNumber())
                .name(heritageEncyclopedia.getName())
                .chineseName(heritageEncyclopedia.getChineseName())
                .cityCode(heritageEncyclopedia.getCityCode())
                .longitude(heritageEncyclopedia.getLongitude())
                .latitude(heritageEncyclopedia.getLatitude())
                .heritageCode(heritageEncyclopedia.getHeritageCode())
                .address(heritageEncyclopedia.getAddress())
                .period(heritageEncyclopedia.getPeriod())
                .imageUrl(heritageEncyclopedia.getImageUrl())
                .content(heritageEncyclopedia.getContent())
                .bookmarkCount(bookmarkCount)
                .isBookmarked(isBookmarked)
                .build();
    }
}
