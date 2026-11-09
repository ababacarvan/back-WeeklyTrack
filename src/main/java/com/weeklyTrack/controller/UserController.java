package com.weeklyTrack.controller;

import com.weeklyTrack.dto.ChangePasswordRequest;
import com.weeklyTrack.dto.CreateUserRequest;
import com.weeklyTrack.dto.UpdateProfileRequest;
import com.weeklyTrack.dto.UserDTO;
import com.weeklyTrack.entity.Role;
import com.weeklyTrack.entity.User;
import com.weeklyTrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD') or @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(
                id,
                user.getFirstName(),
                user.getLastName(),
                user.getTeam() != null ? user.getTeam().getId() : null,
                user.getProject() != null ? user.getProject().getId() : null
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }

    // NOUVEAU: Mettre à jour le profil (prénom, nom)
    @PutMapping("/profile/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD', 'DEVELOPER') and @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<UserDTO> updateProfile(@PathVariable Long id, @Valid @RequestBody UpdateProfileRequest request) {
        User updatedUser = userService.updateProfile(id, request.getFirstName(), request.getLastName());
        return ResponseEntity.ok(convertToDTO(updatedUser));
    }

    // NOUVEAU: Changer le mot de passe
    @PostMapping("/change-password/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD', 'DEVELOPER') and @userSecurity.isCurrentUser(#id)")
    public ResponseEntity<?> changePassword(@PathVariable Long id, @Valid @RequestBody ChangePasswordRequest request) {
        userService.changePassword(id, request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok().body("Mot de passe changé avec succès");
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserRequest request) {
        User created = userService.createUser(
                request.getEmail(),
                request.getPassword() != null && !request.getPassword().isEmpty() ? request.getPassword() : "password123",
                request.getFirstName(),
                request.getLastName(),
                request.getRole(),
                request.getTeamId(),
                request.getProjectId()
        );
        return ResponseEntity.ok(convertToDTO(created));
    }

    // Ajoutez cette méthode helper
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setFullName(user.getFullName());
        dto.setRole(user.getRole());
        dto.setActive(user.isActive());

        if (user.getTeam() != null) {
            dto.setTeamId(user.getTeam().getId());
            dto.setTeamName(user.getTeam().getName());
        }

        if (user.getProject() != null) {
            dto.setProjectId(user.getProject().getId());
            dto.setProjectName(user.getProject().getName());
        }

        return dto;
    }
}