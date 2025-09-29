package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="members")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 50)
    private String email;
    @Column(nullable = false, length = 30)
    private String password;
    @Column(nullable = false, length = 20)
    private String phoneNumber;
    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.ROLE_USER;
    @Column(nullable = false)
    private String state;
    @JoinColumn(name="profile_picture", nullable = true, foreignKey = @ForeignKey(name = "fk_member_profile_picture"))
    private ProfilePicture profilePicture;
}
