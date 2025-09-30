package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="hunter_event_pictures")
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Slf4j
public class hunterEventPicture {
    //id, 자동증가, primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    // 원본이름 길이255 not null
    @Column(name="original_name", length=255 ,nullable = false)
    private String originalName;
    // 저장이름 길이255 not null
    @Column(name="saved_name", length = 255, nullable = false)
    private String savedName;
    // 경로, 길이500, not null
    @Column(name="path", length = 500, nullable = false)
    private String path;
    //파일 크기, not null
    @Column(name="size", nullable = false)
    private Long size;
    //파일 확장자, 길이10, not null
    @Column(name="file_extension", length=10,nullable = false)
    private String fileExtension;
    // 생성시간, 생성시간자동생성
    @Column(name = "created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
    // 외래키, 다대일관계, not null, on delete cascade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="hunter_event_id", foreignKey = @ForeignKey(name = "fk_hunter_event_id"), nullable = false)
            @OnDelete(action = OnDeleteAction.CASCADE)
    private HunterEvent hunterEvents;
}
