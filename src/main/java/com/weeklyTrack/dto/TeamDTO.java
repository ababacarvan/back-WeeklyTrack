// com.weeklyTrack.dto.TeamDTO.java
package com.weeklyTrack.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TeamDTO {
    private Long id;
    private String name;
    private String description;
    private Long techLeadId;
    private String techLeadName;
    private LocalDateTime createdAt;
}