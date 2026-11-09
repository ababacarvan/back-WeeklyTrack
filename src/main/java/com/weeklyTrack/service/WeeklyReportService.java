package com.weeklyTrack.service;

import com.weeklyTrack.dto.WeeklyReportDTO;
import com.weeklyTrack.dto.WeeklyReportRequest;
import com.weeklyTrack.dto.WeeklyReportResponseDTO;
import com.weeklyTrack.entity.*;
import com.weeklyTrack.repository.WeeklyReportRepository;
import com.weeklyTrack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeeklyReportService {

    @Autowired
    private WeeklyReportRepository reportRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NotificationService notificationService;

    @Transactional
    public WeeklyReport createOrUpdateDraft(Long userId, WeeklyReportRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeeklyReport report = reportRepository
                .findByUserAndYearAndWeekNumber(user, request.getYear(), request.getWeekNumber())
                .orElse(new WeeklyReport());

        report.setUser(user);
        report.setYear(request.getYear());
        report.setWeekNumber(request.getWeekNumber());
        report.setHighlights(request.getHighlights());
        report.setCompletedTasks(request.getCompletedTasks());
        report.setPlannedTasks(request.getPlannedTasks());
        report.setBlockers(request.getBlockers());
        report.setAdditionalComments(request.getAdditionalComments());

        if (report.getId() == null) {
            report.setStatus(ReportStatus.DRAFT);
        }

        return reportRepository.save(report);
    }

    @Transactional
    public WeeklyReport submitReport(Long userId, Integer year, Integer weekNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeeklyReport report = reportRepository
                .findByUserAndYearAndWeekNumber(user, year, weekNumber)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        if (report.getStatus() == ReportStatus.VALIDATED) {
            throw new RuntimeException("Cannot submit a validated report");
        }

        report.setStatus(ReportStatus.SUBMITTED);
        report.setSubmittedAt(LocalDateTime.now());

        WeeklyReport savedReport = reportRepository.save(report);

        // Notifier le Tech Lead
        if (user.getTeam() != null && user.getTeam().getTechLead() != null) {
            notificationService.sendNotification(
                    user.getTeam().getTechLead(),
                    "Nouveau reporting soumis",
                    String.format("Le reporting de %s pour la semaine %d a été soumis",
                            user.getFullName(), weekNumber),
                    NotificationType.REPORT_SUBMITTED,
                    savedReport.getId()
            );
        }

        return savedReport;
    }

    @Transactional
    public WeeklyReport validateReport(Long techLeadId, Long reportId, String rejectionReason) {
        WeeklyReport report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("Report not found"));

        User techLead = userRepository.findById(techLeadId)
                .orElseThrow(() -> new RuntimeException("Tech Lead not found"));

        if (report.getStatus() != ReportStatus.SUBMITTED) {
            throw new RuntimeException("Only submitted reports can be validated/rejected");
        }

        if (rejectionReason != null && !rejectionReason.isEmpty()) {
            report.setStatus(ReportStatus.REJECTED);
            report.setRejectionReason(rejectionReason);
            notificationService.sendNotification(
                    report.getUser(),
                    "Reporting rejeté",
                    String.format("Votre reporting pour la semaine %d a été rejeté. Raison: %s",
                            report.getWeekNumber(), rejectionReason),
                    NotificationType.REPORT_REJECTED,
                    report.getId()
            );
        } else {
            report.setStatus(ReportStatus.VALIDATED);
            report.setValidatedBy(techLead);
            report.setValidatedAt(LocalDateTime.now());
            notificationService.sendNotification(
                    report.getUser(),
                    "Reporting validé",
                    String.format("Votre reporting pour la semaine %d a été validé", report.getWeekNumber()),
                    NotificationType.REPORT_VALIDATED,
                    report.getId()
            );
        }

        return reportRepository.save(report);
    }

    public List<WeeklyReportDTO> getReportsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return reportRepository.findByUser(user).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<WeeklyReportDTO> getReportsByTeam(Long teamId) {
        return reportRepository.findByTeamId(teamId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<WeeklyReportDTO> getReportsByStatus(ReportStatus status) {
        return reportRepository.findByStatus(status).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public WeeklyReportDTO getReportByUserAndWeek(Long userId, Integer year, Integer weekNumber) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        WeeklyReport report = reportRepository
                .findByUserAndYearAndWeekNumber(user, year, weekNumber)
                .orElse(null);

        return report != null ? convertToDTO(report) : null;
    }

    private WeeklyReportDTO convertToDTO(WeeklyReport report) {
        WeeklyReportDTO dto = new WeeklyReportDTO();
        dto.setId(report.getId());
        dto.setUserId(report.getUser().getId());
        dto.setUserFullName(report.getUser().getFullName());
        dto.setWeekNumber(report.getWeekNumber());
        dto.setYear(report.getYear());
        dto.setHighlights(report.getHighlights());
        dto.setCompletedTasks(report.getCompletedTasks());
        dto.setPlannedTasks(report.getPlannedTasks());
        dto.setBlockers(report.getBlockers());
        dto.setAdditionalComments(report.getAdditionalComments());
        dto.setStatus(report.getStatus());
        dto.setRejectionReason(report.getRejectionReason());
        dto.setSubmittedAt(report.getSubmittedAt());
        dto.setValidatedAt(report.getValidatedAt());

        if (report.getValidatedBy() != null) {
            dto.setValidatedBy(report.getValidatedBy().getFullName());
        }

        return dto;
    }
    public WeeklyReportResponseDTO convertToResponseDTO(WeeklyReport report) {
        if (report == null) return null;

        WeeklyReportResponseDTO dto = new WeeklyReportResponseDTO();
        dto.setId(report.getId());
        dto.setUserId(report.getUser().getId());
        dto.setUserFullName(report.getUser().getFullName());
        dto.setWeekNumber(report.getWeekNumber());
        dto.setYear(report.getYear());
        dto.setHighlights(report.getHighlights());
        dto.setCompletedTasks(report.getCompletedTasks());
        dto.setPlannedTasks(report.getPlannedTasks());
        dto.setBlockers(report.getBlockers());
        dto.setAdditionalComments(report.getAdditionalComments());
        dto.setStatus(report.getStatus());
        dto.setRejectionReason(report.getRejectionReason());
        dto.setSubmittedAt(report.getSubmittedAt());
        dto.setValidatedAt(report.getValidatedAt());

        if (report.getValidatedBy() != null) {
            dto.setValidatedBy(report.getValidatedBy().getFullName());
        }

        return dto;
    }
    // Ajoutez ces méthodes dans WeeklyReportService.java

    public List<WeeklyReportDTO> getPendingReportsByTeam(Long teamId) {
        return reportRepository.findByTeamIdAndStatus(teamId, ReportStatus.SUBMITTED).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Long getTeamIdByTechLead(Long techLeadId) {
        User techLead = userRepository.findById(techLeadId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (techLead.getTeam() != null) {
            return techLead.getTeam().getId();
        }
        return null;
    }

    public List<WeeklyReportDTO> getAllReportsForValidation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<WeeklyReport> reports;

        switch (user.getRole()) {
            case ADMIN:
            case DIRECTOR:
                // Voir tous les reports soumis
                reports = reportRepository.findByStatus(ReportStatus.SUBMITTED);
                break;
            case TECH_LEAD:
                // Voir les reports de son équipe
                if (user.getTeam() != null) {
                    reports = reportRepository.findByTeamIdAndStatus(user.getTeam().getId(), ReportStatus.SUBMITTED);
                } else {
                    reports = new ArrayList<>();
                }
                break;
            default:
                reports = new ArrayList<>();
        }

        return reports.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    // Dans WeeklyReportService.java, ajoutez :

    public List<WeeklyReportDTO> getAllReports() {
        return reportRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}

