package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 도감 북마크 entity
// (member_id, encyclopedia_id) unique 처리
@Entity
@Table(
        name = "encyclopedia_bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "encyclopedia_id"})
        }
)
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EncyclopediaBookmark {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @CreationTimestamp
    @Column(name="created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    // 회원 entity를 foreignKey 처리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "member_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookmark_member")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    // 도감 entity를 foreignKey 처리
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "encyclopedia_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_bookmark_encyclopedia")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private HeritageEncyclopedia heritageEncyclopedia;
}
