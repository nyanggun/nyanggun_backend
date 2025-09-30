package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="hunter_badge_images")
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@Slf4j
public class HunterBadgeImage {
    // id, 값 자동증가, primary key
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;
    // 저장되는 이름, 길이255, not null
    @Column(name="saved_name", length=255, nullable = false)
    private String savedName;
    // 경로, 길이500, not null
    @Column(name="path", length=500, nullable = false)
    private String path;
    // 파일 확장자, 10, not null
    @Column(name="file_extension", length=10, nullable=false)
    private String fileExtension;

    // 생성시간, not null, 생성시간자동생성
    @Column(name="created_at", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;
}
