package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

// 담소 사진 entity
@Entity
@Table(name = "talk_pictures")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TalkPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;

    @Column(name = "saved_name", nullable = false, length = 255)
    private String savedName;

    @Column(name="path" , nullable = false, length = 500)
    private String path;

    // @Column(columnDefinition = "BIGINT UNSIGNED") unsigned는 SQL 전용이라 다른 DB와 오류가
    // 발생하여 에러가 생길수 있으니 DDL 생성할 때 작성
    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "file_extension", nullable = false, length = 10)
    private String fileExtension;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_picture_talk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;

}
