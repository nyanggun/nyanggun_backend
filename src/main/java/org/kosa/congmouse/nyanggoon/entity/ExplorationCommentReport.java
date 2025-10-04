package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

/**
 * 탐방기 댓글을 신고하는 테이블 입니다.
 */
@Entity
@Table(name="exploration_comment_reports")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString

public class ExplorationCommentReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name="reason", columnDefinition = "TEXT", nullable = false)
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 10)
    @Builder.Default
    private ReportState reportState = ReportState.처리전;

    //댓글 하나는 신고 여러개를 받을 수 있으므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id", nullable=false, foreignKey = @ForeignKey(name="fk_exploration_comment_reports_comment_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private ExplorationComment explorationComment;

    //회원 하나는 신고 여러개를 보낼 수 있으므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable=false, foreignKey = @ForeignKey(name="fk_exploration_comment_reports_member_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Member member;
}
