package com.weeklyTrack.dto;

import com.weeklyTrack.entity.Role;
import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private Role role;
    private Long teamId;
    private String teamName;
    private Long projectId;
    private String projectName;
    private boolean active;
}

