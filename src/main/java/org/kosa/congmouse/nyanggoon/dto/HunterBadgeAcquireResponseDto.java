package org.kosa.congmouse.nyanggoon.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.kosa.congmouse.nyanggoon.entity.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HunterBadgeAcquireResponseDto {
    private Long badgeId;
    private LocalDateTime acquisitionDate;
    private Member member;
    private HunterBadge hunterBadge;

    public static HunterBadgeAcquireResponseDto from(HunterBadgeAquisition hunterBadgeAquisition){
        return HunterBadgeAcquireResponseDto.builder()
                .badgeId(hunterBadgeAquisition.getId())
                .acquisitionDate(hunterBadgeAquisition.getAcquisitionDate())
                .member(hunterBadgeAquisition.getMember())
                .hunterBadge(hunterBadgeAquisition.getHunterBadge())
                .build();

    }
}
