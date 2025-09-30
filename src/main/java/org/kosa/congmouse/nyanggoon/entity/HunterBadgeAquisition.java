package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="badge_aquisitions")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Slf4j
public class HunterBadgeAquisition {
    // id, primary key, 값 자동 증가
    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 획득날짜, acquisition_date, NOT NULL
    @Column(name="acquisition_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime acquisitionDate;

    // 뱃지 획득한 member, 외래키, 1대1관계, NOT NULL
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(name="fk_member_id"), nullable = false)
    @OneToOne(fetch=FetchType.LAZY)
    private Member member;

    // 획득한 뱃지, 외래키, 다대일관계, NOT NULL, on delete cascade
    @JoinColumn(name="hunter_badge_id", foreignKey = @ForeignKey(name="fk_hunter_badge_id"), nullable = false)
    @ManyToOne(fetch=FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private HunterBadge hunterBadge;
}
