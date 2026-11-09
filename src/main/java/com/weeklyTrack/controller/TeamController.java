package com.weeklyTrack.controller;

import com.weeklyTrack.dto.TeamDTO;
import com.weeklyTrack.entity.Team;
import com.weeklyTrack.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/teams")
@CrossOrigin(origins = "*")
public class TeamController {

    private static final Logger log = LoggerFactory.getLogger(TeamController.class);

    @Autowired
    private TeamService teamService;


    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<Team> getTeamById(@PathVariable Long id) {
        return ResponseEntity.ok(teamService.getTeamById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Team> createTeam(@RequestBody Team team) {
        Team created = teamService.createTeam(
                team.getName(),
                team.getDescription(),
                team.getTechLead() != null ? team.getTechLead().getId() : null
        );
        return ResponseEntity.ok(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Team> updateTeam(@PathVariable Long id, @RequestBody Team team) {
        Team updated = teamService.updateTeam(
                id,
                team.getName(),
                team.getDescription(),
                team.getTechLead() != null ? team.getTechLead().getId() : null
        );
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteTeam(@PathVariable Long id) {
        teamService.deleteTeam(id);
        return ResponseEntity.ok().build();
    }
    // com.weeklyTrack.controller.TeamController.java
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<TeamDTO>> getAllTeams() {
        log.info("Récupération de toutes les équipes");
        List<TeamDTO> teams = teamService.getAllTeamsDTO();
        log.info("Nombre d'équipes trouvées: {}", teams.size());
        return ResponseEntity.ok(teams);
    }
}