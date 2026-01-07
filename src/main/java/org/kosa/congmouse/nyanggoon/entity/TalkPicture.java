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

    @Column(name="path" , nullable = false, length = 500)
    private String path;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "talk_id", nullable = false, foreignKey = @ForeignKey(name = "fk_talk_picture_talk"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Talk talk;

}
