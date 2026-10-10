package com.disasterresponse.service;

import com.disasterresponse.dao.*;
import com.disasterresponse.dto.*;
import com.disasterresponse.enums.*;
import com.disasterresponse.model.*;
import com.disasterresponse.util.MultipartUtil;

import java.time.LocalDateTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DisasterReportService {

    private final DisasterReportDAO reportDAO;
    private final EvidenceDAO evidenceDAO;
    private final IncidentUpdateDAO updateDAO;
    private final ResponseAssignmentDAO assignmentDAO;
    private final UserDAO userDAO;
    private final DisasterDAO disasterDAO;
    private final DepartmentDAO departmentDAO;
    private final ResponseTeamDAO teamDAO;
    private final FileStorageService fileStorageService;
    private final NotificationService notificationService;

    public DisasterReportService() {
        this.reportDAO = new DisasterReportDAO();
        this.evidenceDAO = new EvidenceDAO();
        this.updateDAO = new IncidentUpdateDAO();
        this.assignmentDAO = new ResponseAssignmentDAO();
        this.userDAO = new UserDAO();
        this.disasterDAO = new DisasterDAO();
        this.departmentDAO = new DepartmentDAO();
        this.teamDAO = new ResponseTeamDAO();
        this.fileStorageService = new FileStorageService();
        this.notificationService = new NotificationService();
    }

    public ReportResponseDto submitReport(CreateReportRequest request, Long reporterId) {
        User reporter = userDAO.findById(reporterId);
        if (reporter == null) throw new IllegalArgumentException("Reporter user not found.");

        DisasterType dt = disasterDAO.findById(request.getDisasterTypeId());
        if (dt == null) throw new IllegalArgumentException("Disaster type not found.");

        String reportCode = "RPT-" + Year.now().getValue() + "-" + String.format("%04d", (int) (reportDAO.countTotal() + 1));

        DisasterReport report = new DisasterReport();
        report.setReportCode(reportCode);
        report.setReporterId(reporterId);
        report.setDisasterTypeId(request.getDisasterTypeId());
        report.setSeverity(request.getSeverity() != null ? request.getSeverity() : SeverityLevel.MEDIUM);
        report.setStatus(ReportStatus.SUBMITTED);
        report.setTitle(request.getTitle() != null ? request.getTitle().trim() : "Emergency Incident");
        report.setDescription(request.getDescription() != null ? request.getDescription().trim() : "");
        report.setAddress(request.getAddress() != null ? request.getAddress().trim() : "");
        report.setLatitude(request.getLatitude() != null ? request.getLatitude() : 0.0);
        report.setLongitude(request.getLongitude() != null ? request.getLongitude() : 0.0);
        report.setAftermath(request.getAftermath());
        report.setContactPhone(request.getContactPhone() != null ? request.getContactPhone().trim() : reporter.getPhone());

        report = reportDAO.save(report);

        // Add initial timeline update
        updateDAO.save(new IncidentUpdate(report.getId(), reporterId, null, "SUBMITTED", "Incident report filed by reporter."));

        return getReportById(report.getId(), reporterId, true);
    }

    public EvidenceDto uploadEvidence(Long reportId, MultipartUtil.FilePart filePart, Long userId) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        FileStorageService.StoredFile stored = fileStorageService.storeFile(filePart);

        EvidenceCategory cat = EvidenceCategory.IMAGE;
        String mime = stored.getFileType() != null ? stored.getFileType().toLowerCase() : "";
        if (mime.contains("video")) {
            cat = EvidenceCategory.VIDEO;
        } else if (mime.contains("pdf") || mime.contains("document")) {
            cat = EvidenceCategory.DOCUMENT;
        }

        Evidence evidence = new Evidence(reportId, stored.getOriginalFileName(), stored.getStoredFileName(),
                stored.getFileType(), stored.getFileSize(), stored.getFileUrl(), cat);
        evidence = evidenceDAO.save(evidence);

        EvidenceDto dto = new EvidenceDto();
        dto.setId(evidence.getId());
        dto.setReportId(evidence.getReportId());
        dto.setOriginalFileName(evidence.getOriginalFileName());
        dto.setStoredFileName(evidence.getStoredFileName());
        dto.setFileType(evidence.getFileType());
        dto.setFileSize(evidence.getFileSize());
        dto.setFileUrl(evidence.getFileUrl());
        dto.setFileCategory(evidence.getFileCategory());
        dto.setUploadedAt(evidence.getUploadedAt());
        return dto;
    }

    public PageResponse<ReportResponseDto> getMyReports(Long reporterId, int page, int size) {
        List<DisasterReport> list = reportDAO.findMyReports(reporterId, page, size);
        long total = reportDAO.countMyReports(reporterId);
        List<ReportResponseDto> dtos = list.stream().map(this::populateAndMap).collect(Collectors.toList());
        return new PageResponse<>(dtos, total, page, size);
    }

    public ReportResponseDto getReportById(Long reportId, Long userId, boolean isOfficerOrAdmin) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        if (!isOfficerOrAdmin && !report.getReporterId().equals(userId)) {
            throw new IllegalArgumentException("Access denied to report: " + reportId);
        }

        return populateAndMap(report);
    }

    public PageResponse<ReportResponseDto> getAllReports(String status, String severity, String query, int page, int size) {
        List<DisasterReport> list = reportDAO.findAll(status, severity, query, page, size);
        long total = reportDAO.countAll(status, severity, query);
        List<ReportResponseDto> dtos = list.stream().map(this::populateAndMap).collect(Collectors.toList());
        return new PageResponse<>(dtos, total, page, size);
    }

    public List<ReportResponseDto> getMapIncidents() {
        List<DisasterReport> list = reportDAO.findActiveMapIncidents();
        return list.stream().map(this::populateAndMap).collect(Collectors.toList());
    }

    public ReportResponseDto verifyReport(Long reportId, Long officerId) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        reportDAO.verifyReport(reportId, officerId);
        updateDAO.save(new IncidentUpdate(reportId, officerId, report.getStatus().name(), "VERIFIED", "Incident verified by Government Officer."));

        notificationService.createNotification(report.getReporterId(), "Report Verified: " + report.getReportCode(),
                "Your incident report has been officially verified by disaster coordination authorities.",
                NotificationType.REPORT_VERIFIED, String.valueOf(reportId));

        return getReportById(reportId, officerId, true);
    }

    public ReportResponseDto rejectReport(Long reportId, Long officerId, String reason) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        reportDAO.rejectReport(reportId, officerId, reason);
        updateDAO.save(new IncidentUpdate(reportId, officerId, report.getStatus().name(), "REJECTED", "Report rejected. Reason: " + reason));

        notificationService.createNotification(report.getReporterId(), "Report Update: " + report.getReportCode(),
                "Your incident report could not be validated: " + reason,
                NotificationType.REPORT_REJECTED, String.valueOf(reportId));

        return getReportById(reportId, officerId, true);
    }

    public ReportResponseDto assignReport(Long reportId, AssignDepartmentRequest req, Long officerId) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        ResponseAssignment ra = new ResponseAssignment(reportId, req.getDepartmentId(), req.getResponseTeamId(),
                officerId, req.getPriority(), req.getInstructions());
        assignmentDAO.save(ra);

        reportDAO.updateStatus(reportId, ReportStatus.ASSIGNED);
        updateDAO.save(new IncidentUpdate(reportId, officerId, report.getStatus().name(), "ASSIGNED",
                "Assigned to department (ID: " + req.getDepartmentId() + ") for emergency intervention."));

        notificationService.createNotification(report.getReporterId(), "Team Assigned: " + report.getReportCode(),
                "Response teams have been dispatched and coordinated for your incident.",
                NotificationType.DEPARTMENT_ASSIGNED, String.valueOf(reportId));

        return getReportById(reportId, officerId, true);
    }

    public ReportResponseDto updateStatus(Long reportId, UpdateStatusRequest req, Long officerId) {
        DisasterReport report = reportDAO.findById(reportId);
        if (report == null) throw new IllegalArgumentException("Report not found: " + reportId);

        ReportStatus newStatus = req.getStatus();
        reportDAO.updateStatus(reportId, newStatus);

        String remarks = (req.getRemarks() != null && !req.getRemarks().trim().isEmpty()) ?
                req.getRemarks().trim() : "Incident status transitioned to " + newStatus.name();

        updateDAO.save(new IncidentUpdate(reportId, officerId, report.getStatus().name(), newStatus.name(), remarks));

        notificationService.createNotification(report.getReporterId(), "Incident Update: " + report.getReportCode(),
                "Status updated to: " + newStatus.name(),
                NotificationType.STATUS_UPDATE, String.valueOf(reportId));

        return getReportById(reportId, officerId, true);
    }

    private ReportResponseDto populateAndMap(DisasterReport r) {
        ReportResponseDto dto = new ReportResponseDto();
        dto.setId(r.getId());
        dto.setReportCode(r.getReportCode());
        dto.setReporterId(r.getReporterId());
        if (r.getReporter() != null) {
            dto.setReporterName(r.getReporter().getName());
            dto.setReporterEmail(r.getReporter().getEmail());
        }
        dto.setDisasterTypeId(r.getDisasterTypeId());
        if (r.getDisasterType() != null) {
            dto.setDisasterTypeName(r.getDisasterType().getName());
            dto.setDisasterCode(r.getDisasterType().getCode());
        }
        dto.setSeverity(r.getSeverity());
        dto.setStatus(r.getStatus());
        dto.setTitle(r.getTitle());
        dto.setDescription(r.getDescription());
        dto.setAddress(r.getAddress());
        dto.setLatitude(r.getLatitude());
        dto.setLongitude(r.getLongitude());
        dto.setAftermath(r.getAftermath());
        dto.setContactPhone(r.getContactPhone());
        dto.setRejectionReason(r.getRejectionReason());
        dto.setVerifiedById(r.getVerifiedById());
        if (r.getVerifiedBy() != null) {
            dto.setVerifiedByName(r.getVerifiedBy().getName());
        }
        dto.setVerifiedAt(r.getVerifiedAt());
        dto.setCreatedAt(r.getCreatedAt());
        dto.setUpdatedAt(r.getUpdatedAt());
        dto.setOfficialVerified(r.getStatus() == ReportStatus.VERIFIED || r.getStatus() == ReportStatus.ASSIGNED
                || r.getStatus() == ReportStatus.RESPONSE_IN_PROGRESS || r.getStatus() == ReportStatus.RESOLVED);

        // Fetch assignment if exists
        ResponseAssignment ra = assignmentDAO.findByReportId(r.getId());
        if (ra != null) {
            dto.setAssignedDepartmentId(ra.getDepartmentId());
            if (ra.getDepartment() != null) {
                dto.setAssignedDepartmentName(ra.getDepartment().getName());
            }
            dto.setAssignedTeamId(ra.getResponseTeamId());
            if (ra.getResponseTeam() != null) {
                dto.setAssignedTeamName(ra.getResponseTeam().getTeamName());
            }
            dto.setAssignmentPriority(ra.getPriority());
            dto.setAssignmentInstructions(ra.getInstructions());
            dto.setAssignedAt(ra.getAssignedAt());
        }

        // Fetch evidences
        List<Evidence> evidences = evidenceDAO.findByReportId(r.getId());
        List<EvidenceDto> evDtos = evidences.stream().map(e -> {
            EvidenceDto ed = new EvidenceDto();
            ed.setId(e.getId());
            ed.setReportId(e.getReportId());
            ed.setOriginalFileName(e.getOriginalFileName());
            ed.setStoredFileName(e.getStoredFileName());
            ed.setFileType(e.getFileType());
            ed.setFileSize(e.getFileSize());
            ed.setFileUrl(e.getFileUrl());
            ed.setFileCategory(e.getFileCategory());
            ed.setUploadedAt(e.getUploadedAt());
            return ed;
        }).collect(Collectors.toList());
        dto.setEvidences(evDtos);

        // Fetch incident updates
        List<IncidentUpdate> updates = updateDAO.findByReportId(r.getId());
        List<IncidentUpdateDto> upDtos = updates.stream().map(u -> {
            IncidentUpdateDto ud = new IncidentUpdateDto();
            ud.setId(u.getId());
            ud.setReportId(u.getReportId());
            ud.setUpdatedById(u.getUpdatedById());
            if (u.getUpdatedBy() != null) {
                ud.setUpdatedByName(u.getUpdatedBy().getName());
            }
            if (u.getPreviousStatus() != null) {
                try {
                    ud.setPreviousStatus(ReportStatus.valueOf(u.getPreviousStatus()));
                } catch (Exception ignored) {}
            }
            if (u.getNewStatus() != null) {
                try {
                    ud.setNewStatus(ReportStatus.valueOf(u.getNewStatus()));
                } catch (Exception ignored) {}
            }
            ud.setRemarks(u.getRemarks());
            ud.setTimestamp(u.getTimestamp());
            return ud;
        }).collect(Collectors.toList());
        dto.setUpdates(upDtos);

        return dto;
    }
}
