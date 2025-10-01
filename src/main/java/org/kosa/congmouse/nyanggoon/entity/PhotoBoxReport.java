package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "photo_box_bookmark",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"member_id", "photo_box_id"}
                )
        }
)
public class PhotoBoxReport {
    @Column(name="id")
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name="photo_box_id", foreignKey = @ForeignKey(name = "fk_photo_bookmark_member"),nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long photoBoxId;

    @JoinColumn(name="member_id", foreignKey=@ForeignKey(name = "fk_photo_bookmark_member"), nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Long memberId;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
