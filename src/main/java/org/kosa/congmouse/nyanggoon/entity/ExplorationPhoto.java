package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

/**
 * 문화재 탐방기 사진 테이블 입니다.
 */
@Entity
@Table(name="exploration_photos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class ExplorationPhoto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    //탐방기는 1개, 사진은 여러개 가능
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="exploration_id", nullable=false, foreignKey = @ForeignKey(name="fk_exploration_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Exploration exploration;

    @Column(name="path", nullable = false, length = 500)
    private String path;

    // 다대일관계의 exploration 지정
    protected void setExploration(Exploration exploration){
        this.exploration = exploration;
    }
}
