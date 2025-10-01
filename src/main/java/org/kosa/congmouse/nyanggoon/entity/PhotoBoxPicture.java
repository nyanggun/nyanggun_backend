package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="photo_box_pictures")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class PhotoBoxPicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    //게시글 1개당 사진 1개만 가능하다
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="photo_box_id", nullable=false, foreignKey = @ForeignKey(name="fk_photo_box_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private PhotoBox photoBox;

    @Column(name="original_name", nullable = false)
    private String originalName;

    @Column(name="saved_name", nullable = false)
    private String savedName;

    @Column(name="path", nullable = false, length = 500)
    private String path;

    @Column(name="size", nullable = false)
    private Long size;

    @Column(name="file_extension", nullable = false, length = 10)
    private String fileExtension;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

}
