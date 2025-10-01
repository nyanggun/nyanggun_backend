package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 담소 댓글 신고 entity
@Entity
@Table(name = "talk_comment_reports")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TalkCommentReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @Lob
    @Column(name = "reason",nullable = false,columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING) // DB에는 문자열로 저장하도록 지정
    @Column(name = "state", nullable = false, length = 10)
    // DDL의 DEFAULT '처리 전'을 반영
    @Builder.Default
    private ReportState reportState = ReportState.처리전;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_comment_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TalkComment talkComment;
}
