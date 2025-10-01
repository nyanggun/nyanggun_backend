package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 담소 댓글 entity
@Entity
@Table(name = "talk_comments")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TalkComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false,columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_comment_member_id"))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_comment_talk_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_comment_parent_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private TalkComment parentComment;
}
