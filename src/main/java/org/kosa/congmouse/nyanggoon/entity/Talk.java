package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name = "talks")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
//담소 엔티티 입니다.
public class Talk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_member_id"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    // columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP" 을 작성하면 MySQL에서 기본값을 현재시간으로 설정하기에 따로 작성해야한다.
    private LocalDateTime createdAt;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;
    
    //연관 관계 메소드(단방향)
    public void assignAuthor(Member member){ this.member = member; }


}

