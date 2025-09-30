package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * 탐방기 댓글 테이블 입니다.
 */
@Entity
@Table(name = "exploration_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ExplorationComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Lob
    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    //회원1명은 여러 댓글을 달 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_member_id"))
    @OnDelete(action = OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Member member;

    //탐방기 게시글 1개에는 여러 댓글을 달 수 있음
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exploration_id", nullable = false, foreignKey = @ForeignKey(name = "fk_explorations_id"))
    @OnDelete(action = OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Exploration explorations;

    //자기자신을 참조하므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    @OnDelete(action = OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private ExplorationComment parentComment;


}
