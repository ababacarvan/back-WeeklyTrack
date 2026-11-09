// com.weeklytrack.entity.WeeklyReport.java
package com.weeklyTrack.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "weekly_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WeeklyReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private Integer weekNumber;

    @Column(nullable = false)
    private Integer year;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String highlights;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String completedTasks;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String plannedTasks;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String blockers;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String additionalComments;

    @Enumerated(EnumType.STRING)
    private ReportStatus status = ReportStatus.DRAFT;

    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by")
    @JsonIgnore
    private User validatedBy;

    private LocalDateTime submittedAt;

    private LocalDateTime validatedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}