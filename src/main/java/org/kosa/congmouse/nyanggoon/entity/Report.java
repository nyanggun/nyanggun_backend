package org.kosa.congmouse.nyanggoon.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name="content_type", nullable = false)
    private ContentType contentType;

    @Column(name="content_id", nullable = false)
    private Long contentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="report_member_id", nullable=false, foreignKey = @ForeignKey(name="fk_reports_member_id"))
    private Member reportMember;

    @Lob
    @Column(name="reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name="state", nullable = false, length = 10)
    @Builder.Default
    private ReportState reportState = ReportState.PENDING;

    @CreationTimestamp
    @Column(name="created_at", updatable = false)
    private LocalDateTime createdAt;

    public void changeState(ReportState reportState) {
        if(this.reportState == ReportState.PENDING)
            this.reportState = ReportState.PROCESSED;
        else{
            this.reportState = ReportState.PENDING;
        }
    }
}
