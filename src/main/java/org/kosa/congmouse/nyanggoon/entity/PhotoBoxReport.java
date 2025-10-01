package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Table(name = "photo_box_reports")
public class PhotoBoxReport {
    @Column(name="id")
    @Id
    @GeneratedValue
    private Long id;

    @JoinColumn(name="photo_box_id", foreignKey = @ForeignKey(name = "fk_photo_report_box"),nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private PhotoBox photoBox;

    @JoinColumn(name="member_id", foreignKey=@ForeignKey(name = "fk_photo_report_reporter"), nullable = false)
    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member memberId;

    @Column(name="reason", nullable = false)
    @Lob
    private String reason;

    @Column(name="state", length=10, nullable = false)
    @Builder.Default
    @Enumerated(EnumType.STRING)
    private ReportState reportState = ReportState.처리전;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
