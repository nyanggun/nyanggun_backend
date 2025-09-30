package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 담소 북마크 entity
@Entity
@Table(name = "talk_bookmarks", uniqueConstraints = {
        @UniqueConstraint(name = "uq_member_talk", columnNames = {"member_id", "talk_id"})
})
// unique 복합 유니크 제약 조건이 있어서 추가를 위에 해야한다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TalkBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_bookmark_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_bookmark_talk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;
}
