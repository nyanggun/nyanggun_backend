package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 탐방기 북마크를 하는 테이블 입니다.
 */
@Entity
//유니크 제약 조건을 걸어줍니다.
@Table(name="exploration_bookmarks",  uniqueConstraints = {
        @UniqueConstraint(
                name = "uk_member_exploration",
                columnNames = {"member_id", "exploration_id"}
        )
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ExplorationBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    //탐방기 하나는 여러 북마크를 받을 수 있으므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="exploration_id", nullable=false, foreignKey = @ForeignKey(name="fk_exploration_id"))
    private ExplorationComment explorationComment;

    //회원 하나는 북마크를 여러개 선택 할 수 있으므로
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable=false, foreignKey = @ForeignKey(name="fk_member_id"))
    private Member member;

}
