package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.HunterBadge;
import org.kosa.congmouse.nyanggoon.entity.HunterBadgeAcquisition;
import org.kosa.congmouse.nyanggoon.entity.Member;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HunterBadgesAcquisitionResponseDto {
    private Long hunterBadgeAcquisitionId;
    private LocalDateTime acquisitionDate;
    private Long hunterBadgeId;
    private String badgeName;
    private String badgeImg;

    public static HunterBadgesAcquisitionResponseDto from(HunterBadgeAcquisition hunterBadgeAcquisition){
        return HunterBadgesAcquisitionResponseDto.builder()
                .hunterBadgeAcquisitionId(hunterBadgeAcquisition.getId())
                .acquisitionDate(hunterBadgeAcquisition.getAcquisitionDate())
                .hunterBadgeId(hunterBadgeAcquisition.getHunterBadge().getId())
                .badgeName(hunterBadgeAcquisition.getHunterBadge().getName())
                .badgeImg(hunterBadgeAcquisition.getHunterBadge().getImgUrl())
                .build();
    }
}
