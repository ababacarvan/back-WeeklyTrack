package com.weeklyTrack.repository;

import com.weeklyTrack.entity.Project;
import com.weeklyTrack.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    List<Project> findByTeam(Team team);
    List<Project> findByActiveTrue();
}