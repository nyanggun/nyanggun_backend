package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

// 사진함 태그 교차 테이블 entity
@Entity
@Table(name = "photo_box_tags", uniqueConstraints = {
        @UniqueConstraint(name = "uq_photo_box_tag", columnNames = {"photo_box_id", "tag_id"})
})
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PhotoBoxTag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_box_id", nullable = false, foreignKey = @ForeignKey(name = "fk_photo_box_tag_box"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PhotoBox photoBox;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", nullable = false, foreignKey = @ForeignKey(name = "fk_photo_box_tag_tag"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Tag tag;
}
