package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 사진함 북마크 entity
@Entity
@Table(name = "photo_box_bookmarks", uniqueConstraints = {
        @UniqueConstraint(name = "uq_member_photo_box", columnNames = {"member_id", "photo_box_id"})
})
// unique 복합 유니크 제약 조건이 있어서 추가를 위에 해야한다.
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PhotoBoxBookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_photo_bookmark_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_box_id", nullable = false, foreignKey = @ForeignKey(name = "fk_photo_bookmark_box"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PhotoBox photoBox;
}
