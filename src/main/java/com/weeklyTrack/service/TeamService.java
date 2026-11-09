// com.weeklyTrack.service.TeamService.java
package com.weeklyTrack.service;

import com.weeklyTrack.dto.TeamDTO;
import com.weeklyTrack.entity.Team;
import com.weeklyTrack.entity.User;
import com.weeklyTrack.repository.TeamRepository;
import com.weeklyTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Team createTeam(String name, String description, Long techLeadId) {
        Team team = new Team();
        team.setName(name);
        team.setDescription(description);

        if (techLeadId != null) {
            User techLead = userRepository.findById(techLeadId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            team.setTechLead(techLead);
        }

        return teamRepository.save(team);
    }

    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    public Team getTeamById(Long id) {
        return teamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Team not found"));
    }

    @Transactional
    public Team updateTeam(Long id, String name, String description, Long techLeadId) {
        Team team = getTeamById(id);
        team.setName(name);
        team.setDescription(description);

        if (techLeadId != null) {
            User techLead = userRepository.findById(techLeadId)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            team.setTechLead(techLead);
        }

        return teamRepository.save(team);
    }

    @Transactional
    public void deleteTeam(Long id) {
        teamRepository.deleteById(id);
    }
    public List<TeamDTO> getAllTeamsDTO() {
        return teamRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private TeamDTO convertToDTO(Team team) {
        TeamDTO dto = new TeamDTO();
        dto.setId(team.getId());
        dto.setName(team.getName());
        dto.setDescription(team.getDescription());
        dto.setCreatedAt(team.getCreatedAt());

        if (team.getTechLead() != null) {
            dto.setTechLeadId(team.getTechLead().getId());
            dto.setTechLeadName(team.getTechLead().getFirstName() + " " + team.getTechLead().getLastName());
        }

        return dto;
    }
}