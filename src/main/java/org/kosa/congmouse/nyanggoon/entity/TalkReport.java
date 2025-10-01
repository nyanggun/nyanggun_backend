package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 담소 신고 entity
@Entity
@Table(name = "talk_reports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TalkReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @Lob // Large Object - TEXT 타입을
    @Column(name = "reason",nullable = false,columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 10)
    @Builder.Default
    private ReportState reportState = ReportState.처리전; // DEFAULT 값 설정


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_id"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;
}
