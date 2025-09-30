package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="hunter_events")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@ToString
@Slf4j
public class HunterEvent {

    // id, primary key, notnull
    @Column(name="id")
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    //제목, not null 길이255
    @Column(name="title", nullable = false, length=255)
    private String title;
    //이벤트 시작시간 not null
    @Column(name="event_start_time", nullable = false)
    private LocalDateTime eventStartTime;
    // 이벤트 종료 시간, not null
    @Column(name="event_end_time", nullable = false)
    private LocalDateTime eventEndTime;
    // 생성 시간, 생성시간자동생성
    @CreationTimestamp
    @Column(name="created_at", nullable = false)
    private LocalDateTime createdAt;
    // 내용
    @Column(name="content")
    private String content;
    // 작성자, 외래키, not null, on delete cascade
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", foreignKey = @ForeignKey(name = "fk_member_id"), nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Member member_id;
}
