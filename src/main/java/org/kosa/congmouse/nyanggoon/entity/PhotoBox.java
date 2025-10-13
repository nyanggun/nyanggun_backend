package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// 사진함 entity
@Entity
@Table(name = "photo_boxes")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PhotoBox {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @Column(name = "related_heritage",nullable = false,length = 200)
    private String relatedHeritage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_photo_box_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    public void update(String title, String relatedHeritage) {
        // 제목 검증 및 수정
        if (title != null && !title.trim().isEmpty()) {
            this.title = title.trim();
        }
        // 내용 검증 및 수정
        if (relatedHeritage != null && !relatedHeritage.trim().isEmpty()) {
            this.relatedHeritage = relatedHeritage.trim();
        }
    }

    @OneToOne(mappedBy = "photoBox", cascade = CascadeType.ALL, orphanRemoval = true)
    private PhotoBoxPicture picture;

    @OneToMany(mappedBy = "photoBox", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoBoxTag> tags = new ArrayList<>();


}
