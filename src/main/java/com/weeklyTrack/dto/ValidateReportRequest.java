package com.weeklyTrack.dto;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ValidateReportRequest {
    @NotNull
    private Long reportId;

    private String rejectionReason;
}
