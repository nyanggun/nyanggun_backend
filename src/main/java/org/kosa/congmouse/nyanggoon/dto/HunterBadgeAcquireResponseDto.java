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
    private Long hunterBadgeAcquisitionId;
    private LocalDateTime acquisitionDate;
    private Member member;
    private HunterBadge hunterBadge;

    public static HunterBadgeAcquireResponseDto from(HunterBadgeAcquisition hunterBadgeAcquisition){
        return HunterBadgeAcquireResponseDto.builder()
                .hunterBadgeAcquisitionId(hunterBadgeAcquisition.getId())
                .acquisitionDate(hunterBadgeAcquisition.getAcquisitionDate())
                .member(hunterBadgeAcquisition.getMember())
                .hunterBadge(hunterBadgeAcquisition.getHunterBadge())
                .build();

    }
}
