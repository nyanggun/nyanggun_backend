package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.kosa.congmouse.nyanggoon.dto.ExplorationUpdateDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 문화재 탐방기 테이블 입니다.
 */

@Entity
@Table(name="explorations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@ToString
public class Exploration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    // nullable = false : Null 허용하지 않음, 모든 게시물은 작성자가 있어야 함
    @Column(name="title", nullable = false, length=200)
    private String title;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    @Lob
    @Column(name="content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name="related_heritage", nullable = false, length = 200)
    private String relatedHeritage;

    //회원은 1명, 탐방기는 여러 개 작성 가능
    //따라서 Fetch join으로 회원 정보를 가져옵니다.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_id", nullable=false, foreignKey = @ForeignKey(name="fk_explorations_member_id"))
    @OnDelete(action= OnDeleteAction.CASCADE) //DB 차원의 ON DELETE CASCADE 와 동일
    private Member member;

    public void update(ExplorationUpdateDto explorationUpdateDto) {
        title = explorationUpdateDto.getTitle();
        content = explorationUpdateDto.getContent();
        relatedHeritage = explorationUpdateDto.getRelatedHeritage();
    }

    // 문화재탐방기와 문화재탐방기사진은 일대다 관계
    @OneToMany(mappedBy = "exploration", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExplorationPhoto> explorationPhotos = new ArrayList<>();

    // 미리 만들어 두어야 하는 연관관계 편의 메소드
    public void addPhoto(ExplorationPhoto photo) {
        this.explorationPhotos.add(photo); // 1. 내 리스트에 사진 추가
        photo.setExploration(this);      // 2. 사진 객체에도 내가 부모라고 알려주기
    }
}
