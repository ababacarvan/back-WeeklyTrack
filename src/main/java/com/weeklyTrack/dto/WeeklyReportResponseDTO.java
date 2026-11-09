// com.weeklyTrack.dto.WeeklyReportResponseDTO.java
package com.weeklyTrack.dto;

import com.weeklyTrack.entity.ReportStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class WeeklyReportResponseDTO {
    private Long id;
    private Long userId;
    private String userFullName;
    private Integer weekNumber;
    private Integer year;
    private String highlights;
    private String completedTasks;
    private String plannedTasks;
    private String blockers;
    private String additionalComments;
    private ReportStatus status;
    private String rejectionReason;
    private String validatedBy;
    private LocalDateTime submittedAt;
    private LocalDateTime validatedAt;
}