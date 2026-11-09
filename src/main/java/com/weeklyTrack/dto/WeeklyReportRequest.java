package com.weeklyTrack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class WeeklyReportRequest {
    @NotNull
    @Min(1)
    private Integer weekNumber;

    @NotNull
    @Min(2024)
    private Integer year;

    private String highlights;
    private String completedTasks;
    private String plannedTasks;
    private String blockers;
    private String additionalComments;
}
