package com.weeklyTrack.repository;

import com.weeklyTrack.entity.User;
import com.weeklyTrack.entity.Role;
import com.weeklyTrack.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    List<User> findByTeam(Team team);
    List<User> findByActiveTrue();
}
