package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 문화재 탐방기 테이블 입니다.
 */

@Entity
@Table(name="explorations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Exploration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // nullable = false : Null 허용하지 않음, 모든 게시물은 작성자가 있어야 함
    @Column(name="title", nullable = false, length=200)
    private String title;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name="content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="related_heritage", nullable = false, length = 200)
    private String relatedHeritage;

    //회원은 1명, 탐방기는 여러 개 작성 가능
    //따라서 Fetch join으로 회원 정보를 가져옵니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable=false, foreignKey = @ForeignKey(name="fk_explorations_member_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Member member;
}
