package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import jdk.jfr.Unsigned;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.data.annotation.CreatedBy;

import java.time.LocalDateTime;

// 프로필 사진 entity
@Entity
@Table(name="profile_pictures")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class ProfilePicture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "original_name", nullable = false, length = 255)
    private String originalName;
    @Column(name = "saved_name", nullable = false, length = 255)
    private String savedName;
    @Column(name = "path", nullable = false, length = 500)
    private String path;
    @Column(name = "size", nullable = false)
    private Long size;
    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "file_extension", nullable = false, length = 10)
    private String fileExtension;
    // 회원 entity를 foreignKey 처리
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", unique = true, nullable = false, foreignKey = @ForeignKey(name="fk_profile_picture_member"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    // 프로필사진을 가진 회원 세팅
    public void profileOwner(Member member){
        this.member = member;
    }
}
