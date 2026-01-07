package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import jdk.jfr.Timestamp;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.dto.MemberResponseDto;
import org.kosa.congmouse.nyanggoon.dto.MemberUpdateRequestDto;

import java.time.LocalDateTime;
// 사냥꾼(멤버) entity
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
    @Column(name="id")
    private Long id;
    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;
    @Column(name = "password", nullable = false, length = 255)
    private String password;
    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;
    @Column(name = "phone_number", nullable = false, length = 20)
    private String phoneNumber;
    @CreationTimestamp
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;
    // role 컬럼은 enum 타입으로 처리 : ROLE_USER, ROLE_ADMIN
    @Column(name = "role", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberRole role = MemberRole.ROLE_USER;
    // state 컬럼은 enum 타입으로 처리 : ACTIVE, DISABLE
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private MemberState memberstate = MemberState.ACTIVE;

    // 프로필 사진 entity 변경 -> 사진 경로만
    @Column(name="path", nullable = true)
    private String path;

    public void changeMemberState() {
        if(this.memberstate == MemberState.ACTIVE)
            this.memberstate = MemberState.DISABLED;
        else
            this.memberstate = MemberState.ACTIVE;
    }
    public void changeMemberInfo(MemberUpdateRequestDto memberUpdateRequestDto){
        this.nickname = memberUpdateRequestDto.getNickname();
        this.phoneNumber = memberUpdateRequestDto.getPhoneNumber();
    }
    public void setPassword(String encodedPassword) {
        this.password = encodedPassword;
    }


    public void updateInfo(String email, String nickname, String phoneNumber, String path) {
        this.email = email;
        this.nickname = nickname;
        this.phoneNumber = phoneNumber;
        this.path = path;
    }
}
