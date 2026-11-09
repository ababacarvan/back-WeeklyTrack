package com.weeklyTrack.controller;

import com.weeklyTrack.dto.WeeklyReportDTO;
import com.weeklyTrack.dto.WeeklyReportRequest;
import com.weeklyTrack.dto.WeeklyReportResponseDTO;
import com.weeklyTrack.dto.ValidateReportRequest;
import com.weeklyTrack.entity.ReportStatus;
import com.weeklyTrack.entity.WeeklyReport;
import com.weeklyTrack.security.CurrentUser;
import com.weeklyTrack.security.UserPrincipal;
import com.weeklyTrack.service.ExportService;
import com.weeklyTrack.service.WeeklyReportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class WeeklyReportController {

    @Autowired
    private WeeklyReportService reportService;

    @Autowired
    private ExportService exportService;

    @PostMapping("/draft")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<WeeklyReportResponseDTO> saveDraft(@CurrentUser UserPrincipal currentUser,
                                                             @Valid @RequestBody WeeklyReportRequest request) {
        WeeklyReport report = reportService.createOrUpdateDraft(currentUser.getId(), request);
        return ResponseEntity.ok(reportService.convertToResponseDTO(report));
    }

    @PostMapping("/submit")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<WeeklyReportResponseDTO> submitReport(@CurrentUser UserPrincipal currentUser,
                                                                @RequestParam Integer year,
                                                                @RequestParam Integer weekNumber) {
        WeeklyReport report = reportService.submitReport(currentUser.getId(), year, weekNumber);
        return ResponseEntity.ok(reportService.convertToResponseDTO(report));
    }

    @PostMapping("/validate")
    @PreAuthorize("hasAnyRole('TECH_LEAD', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<WeeklyReportResponseDTO> validateReport(@CurrentUser UserPrincipal currentUser,
                                                                  @Valid @RequestBody ValidateReportRequest request) {
        WeeklyReport report = reportService.validateReport(
                currentUser.getId(),
                request.getReportId(),
                request.getRejectionReason()
        );
        return ResponseEntity.ok(reportService.convertToResponseDTO(report));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<List<WeeklyReportDTO>> getUserReports(@PathVariable Long userId) {
        return ResponseEntity.ok(reportService.getReportsByUser(userId));
    }

    @GetMapping("/team/{teamId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<WeeklyReportDTO>> getTeamReports(@PathVariable Long teamId) {
        return ResponseEntity.ok(reportService.getReportsByTeam(teamId));
    }

    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<WeeklyReportDTO>> getReportsByStatus(@PathVariable ReportStatus status) {
        return ResponseEntity.ok(reportService.getReportsByStatus(status));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<List<WeeklyReportDTO>> getMyReports(@CurrentUser UserPrincipal currentUser) {
        return ResponseEntity.ok(reportService.getReportsByUser(currentUser.getId()));
    }

    @GetMapping("/my/week")
    @PreAuthorize("hasRole('DEVELOPER')")
    public ResponseEntity<WeeklyReportResponseDTO> getMyReportByWeek(@CurrentUser UserPrincipal currentUser,
                                                                     @RequestParam Integer year,
                                                                     @RequestParam Integer weekNumber) {
        WeeklyReportDTO reportDTO = reportService.getReportByUserAndWeek(currentUser.getId(), year, weekNumber);
        if (reportDTO == null) {
            return ResponseEntity.ok(null);
        }
        // Convertir WeeklyReportDTO en WeeklyReportResponseDTO
        WeeklyReportResponseDTO responseDTO = new WeeklyReportResponseDTO();
        responseDTO.setId(reportDTO.getId());
        responseDTO.setUserId(reportDTO.getUserId());
        responseDTO.setUserFullName(reportDTO.getUserFullName());
        responseDTO.setWeekNumber(reportDTO.getWeekNumber());
        responseDTO.setYear(reportDTO.getYear());
        responseDTO.setHighlights(reportDTO.getHighlights());
        responseDTO.setCompletedTasks(reportDTO.getCompletedTasks());
        responseDTO.setPlannedTasks(reportDTO.getPlannedTasks());
        responseDTO.setBlockers(reportDTO.getBlockers());
        responseDTO.setAdditionalComments(reportDTO.getAdditionalComments());
        responseDTO.setStatus(reportDTO.getStatus());
        responseDTO.setRejectionReason(reportDTO.getRejectionReason());
        responseDTO.setValidatedBy(reportDTO.getValidatedBy());
        responseDTO.setSubmittedAt(reportDTO.getSubmittedAt());
        responseDTO.setValidatedAt(reportDTO.getValidatedAt());
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<byte[]> exportToExcel(@RequestParam(required = false) Long teamId,
                                                @RequestParam(required = false) ReportStatus status) {
        List<WeeklyReportDTO> reports;
        if (teamId != null) {
            reports = reportService.getReportsByTeam(teamId);
        } else if (status != null) {
            reports = reportService.getReportsByStatus(status);
        } else {
            reports = reportService.getReportsByStatus(ReportStatus.VALIDATED);
        }

        byte[] excelFile = exportService.exportToExcel(reports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelFile);
    }

    @GetMapping("/export/pdf")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<byte[]> exportToPdf(@RequestParam(required = false) Long teamId,
                                              @RequestParam(required = false) ReportStatus status) {
        List<WeeklyReportDTO> reports;
        if (teamId != null) {
            reports = reportService.getReportsByTeam(teamId);
        } else if (status != null) {
            reports = reportService.getReportsByStatus(status);
        } else {
            reports = reportService.getReportsByStatus(ReportStatus.VALIDATED);
        }

        byte[] pdfFile = exportService.exportToPdf(reports);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=reports.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfFile);
    }
    // Ajoutez ces méthodes dans WeeklyReportController.java

    @GetMapping("/pending")
    @PreAuthorize("hasAnyRole('TECH_LEAD', 'DIRECTOR', 'ADMIN')")
    public ResponseEntity<List<WeeklyReportDTO>> getPendingReports(@CurrentUser UserPrincipal currentUser) {
        List<WeeklyReportDTO> pendingReports;

        // Si c'est un TECH_LEAD, il voit les reports de son équipe uniquement
        if (currentUser.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_TECH_LEAD"))) {
            Long teamId = reportService.getTeamIdByTechLead(currentUser.getId());
            if (teamId != null) {
                pendingReports = reportService.getPendingReportsByTeam(teamId);
            } else {
                pendingReports = reportService.getReportsByStatus(ReportStatus.SUBMITTED);
            }
        } else {
            // ADMIN ou DIRECTOR voient tous les reports soumis
            pendingReports = reportService.getReportsByStatus(ReportStatus.SUBMITTED);
        }

        return ResponseEntity.ok(pendingReports);
    }

    @GetMapping("/all/submitted")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<List<WeeklyReportDTO>> getAllSubmittedReports() {
        return ResponseEntity.ok(reportService.getReportsByStatus(ReportStatus.SUBMITTED));
    }
    // Ajoutez cette méthode dans WeeklyReportController.java

    @GetMapping("/validated")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<WeeklyReportDTO>> getValidatedReports() {
        return ResponseEntity.ok(reportService.getReportsByStatus(ReportStatus.VALIDATED));
    }
    @GetMapping("/all")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR')")
    public ResponseEntity<List<WeeklyReportDTO>> getAllReports() {
        return ResponseEntity.ok(reportService.getAllReports());
    }

    @GetMapping("/team/{teamId}/pending")
    @PreAuthorize("hasAnyRole('ADMIN', 'DIRECTOR', 'TECH_LEAD')")
    public ResponseEntity<List<WeeklyReportDTO>> getTeamPendingReports(@PathVariable Long teamId) {
        return ResponseEntity.ok(reportService.getPendingReportsByTeam(teamId));
    }
}