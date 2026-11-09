package com.weeklyTrack.repository;
import com.weeklyTrack.entity.WeeklyReport;
import com.weeklyTrack.entity.ReportStatus;
import com.weeklyTrack.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface WeeklyReportRepository extends JpaRepository<WeeklyReport, Long> {
    List<WeeklyReport> findByUser(User user);
    List<WeeklyReport> findByUserAndYear(User user, Integer year);
    Optional<WeeklyReport> findByUserAndYearAndWeekNumber(User user, Integer year, Integer weekNumber);
    List<WeeklyReport> findByStatus(ReportStatus status);
    List<WeeklyReport> findByStatusIn(List<ReportStatus> statuses);
    List<WeeklyReport> findByValidatedBy(User validatedBy);

    @Query("SELECT wr FROM WeeklyReport wr WHERE wr.user.team.id = :teamId")
    List<WeeklyReport> findByTeamId(@Param("teamId") Long teamId);

    @Query("SELECT wr FROM WeeklyReport wr WHERE wr.user.team.techLead = :techLead")
    List<WeeklyReport> findByTechLead(@Param("techLead") User techLead);
    // Ajoutez dans WeeklyReportRepository.java
    @Query("SELECT wr FROM WeeklyReport wr WHERE wr.user.team.id = :teamId AND wr.status = :status")
    List<WeeklyReport> findByTeamIdAndStatus(@Param("teamId") Long teamId, @Param("status") ReportStatus status);

    @Query("SELECT COUNT(wr) FROM WeeklyReport wr WHERE wr.user = :user AND wr.year = :year AND wr.weekNumber = :weekNumber")
    long countByUserAndYearAndWeekNumber(@Param("user") User user, @Param("year") Integer year, @Param("weekNumber") Integer weekNumber);
}
