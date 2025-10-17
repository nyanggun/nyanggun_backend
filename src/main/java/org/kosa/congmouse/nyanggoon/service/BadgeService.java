package org.kosa.congmouse.nyanggoon.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.kosa.congmouse.nyanggoon.dto.HunterBadgeAcquireResponseDto;
import org.kosa.congmouse.nyanggoon.dto.HunterBadgeMarkerResponseDto;
import org.kosa.congmouse.nyanggoon.entity.HunterBadge;
import org.kosa.congmouse.nyanggoon.entity.HunterBadgeAcquisition;
import org.kosa.congmouse.nyanggoon.entity.Member;
import org.kosa.congmouse.nyanggoon.repository.HunterBadgeAcquisitionRepository;
import org.kosa.congmouse.nyanggoon.repository.HunterBadgeRepository;
import org.kosa.congmouse.nyanggoon.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class BadgeService {

    private final HunterBadgeRepository hunterBadgeRepository;
    private final HunterBadgeAcquisitionRepository hunterBadgeAcquisitionRepository;
    private final MemberRepository memberRepository;

    /**
     * 지도에 증표 표시하는 메서드
     * @return
     */
    public List<HunterBadgeMarkerResponseDto> getHunterBadgeList() {
        return hunterBadgeRepository.findAll().stream()
                .map(HunterBadgeMarkerResponseDto::from)
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * 획득한 증표를 저장하는 메서드
     *
     * @param badgeId
     * @param memberId
     * @return
     */
    @Transactional
    public HunterBadgeAcquireResponseDto saveAquiredBadge(Long badgeId, Long memberId) {
        log.info("증표 획득 시도:{}", badgeId);
        // 1. 획득 badge 중복 체크
        boolean exists = hunterBadgeAcquisitionRepository.existsByMemberIdAndHunterBadgeId(badgeId, memberId);
        if(exists){
            log.warn("증표 획득 실패 : 이미 획득한 증표");
            throw new IllegalArgumentException("이미 획득한 증표입니다");
        }
        // 2. 존재하는 회원인지, 존재하는 증표인지 확인
        log.info("badgeId {}, check {} ", badgeId, hunterBadgeRepository.findById(badgeId));
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        HunterBadge hunterBadge = hunterBadgeRepository.findById(badgeId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 증표입니다."));

        // 3. 획득한 증표를 증표함에 save
        HunterBadgeAcquisition newAquiredBadge = HunterBadgeAcquisition.builder()
                                                .member(member)
                                                .hunterBadge(hunterBadge)
                                                .build();
        HunterBadgeAcquisition savedAquiredBadge = hunterBadgeAcquisitionRepository.save(newAquiredBadge);
        return HunterBadgeAcquireResponseDto.from(savedAquiredBadge);
    }

    /**
     * 지도에 표시할 이미 획득한 증표(유저에 따라)
     * @param memberId
     * @return
     */
    public List<Long> findAcquiredBadgesList(Long memberId) {
        return hunterBadgeAcquisitionRepository.findBadgeIdsByMemberId(memberId);
    }
}
