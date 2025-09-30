package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;

@Entity
@Table(name="hunter_badges")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Slf4j
public class HunterBadge {
    // id, primary key, 값 자동 증가
    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 배지 이름, 길이20, not null
    @Column(name="name", length=20, nullable = false)
    private String name;

    // 위도, not null
    @Column(name="latitude", nullable = false)
    private BigDecimal latitude;

    // 경도, not null
    @Column(name="longitude", nullable = false)
    private BigDecimal longitude;

    // 헌터뱃지이미지, 일대일관계, 외래키, not null, on delete cascade
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="badge_image_id", foreignKey = @ForeignKey(name = "fk_badge_image_id"),nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private HunterBadgeImage badge_image_id;
}
