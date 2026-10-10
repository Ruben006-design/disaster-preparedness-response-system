package com.disasterresponse.dto;

import com.disasterresponse.enums.ReportStatus;
import com.disasterresponse.enums.SeverityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportResponseDto {
    private Long id;
    private String reportCode;
    private Long reporterId;
    private String reporterName;
    private String reporterEmail;
    private Long disasterTypeId;
    private String disasterTypeName;
    private String disasterCode;
    private SeverityLevel severity;
    private ReportStatus status;
    private String title;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String aftermath;
    private String contactPhone;
    private String rejectionReason;
    private Long verifiedById;
    private String verifiedByName;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean officialVerified;

    // Assignment summary if assigned
    private Long assignedDepartmentId;
    private String assignedDepartmentName;
    private Long assignedTeamId;
    private String assignedTeamName;
    private SeverityLevel assignmentPriority;
    private String assignmentInstructions;
    private LocalDateTime assignedAt;

    private List<EvidenceDto> evidences = new ArrayList<>();
    private List<IncidentUpdateDto> updates = new ArrayList<>();

    public ReportResponseDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportCode() {
        return reportCode;
    }

    public void setReportCode(String reportCode) {
        this.reportCode = reportCode;
    }

    public Long getReporterId() {
        return reporterId;
    }

    public void setReporterId(Long reporterId) {
        this.reporterId = reporterId;
    }

    public String getReporterName() {
        return reporterName;
    }

    public void setReporterName(String reporterName) {
        this.reporterName = reporterName;
    }

    public String getReporterEmail() {
        return reporterEmail;
    }

    public void setReporterEmail(String reporterEmail) {
        this.reporterEmail = reporterEmail;
    }

    public Long getDisasterTypeId() {
        return disasterTypeId;
    }

    public void setDisasterTypeId(Long disasterTypeId) {
        this.disasterTypeId = disasterTypeId;
    }

    public String getDisasterTypeName() {
        return disasterTypeName;
    }

    public void setDisasterTypeName(String disasterTypeName) {
        this.disasterTypeName = disasterTypeName;
    }

    public String getDisasterCode() {
        return disasterCode;
    }

    public void setDisasterCode(String disasterCode) {
        this.disasterCode = disasterCode;
    }

    public SeverityLevel getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityLevel severity) {
        this.severity = severity;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
        this.officialVerified = (status == ReportStatus.VERIFIED ||
                status == ReportStatus.ASSIGNED ||
                status == ReportStatus.RESPONSE_IN_PROGRESS ||
                status == ReportStatus.SITUATION_CONTROLLED ||
                status == ReportStatus.RESOLVED);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAftermath() {
        return aftermath;
    }

    public void setAftermath(String aftermath) {
        this.aftermath = aftermath;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public Long getVerifiedById() {
        return verifiedById;
    }

    public void setVerifiedById(Long verifiedById) {
        this.verifiedById = verifiedById;
    }

    public String getVerifiedByName() {
        return verifiedByName;
    }

    public void setVerifiedByName(String verifiedByName) {
        this.verifiedByName = verifiedByName;
    }

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isOfficialVerified() {
        return officialVerified;
    }

    public void setOfficialVerified(boolean officialVerified) {
        this.officialVerified = officialVerified;
    }

    public Long getAssignedDepartmentId() {
        return assignedDepartmentId;
    }

    public void setAssignedDepartmentId(Long assignedDepartmentId) {
        this.assignedDepartmentId = assignedDepartmentId;
    }

    public String getAssignedDepartmentName() {
        return assignedDepartmentName;
    }

    public void setAssignedDepartmentName(String assignedDepartmentName) {
        this.assignedDepartmentName = assignedDepartmentName;
    }

    public Long getAssignedTeamId() {
        return assignedTeamId;
    }

    public void setAssignedTeamId(Long assignedTeamId) {
        this.assignedTeamId = assignedTeamId;
    }

    public String getAssignedTeamName() {
        return assignedTeamName;
    }

    public void setAssignedTeamName(String assignedTeamName) {
        this.assignedTeamName = assignedTeamName;
    }

    public SeverityLevel getAssignmentPriority() {
        return assignmentPriority;
    }

    public void setAssignmentPriority(SeverityLevel assignmentPriority) {
        this.assignmentPriority = assignmentPriority;
    }

    public String getAssignmentInstructions() {
        return assignmentInstructions;
    }

    public void setAssignmentInstructions(String assignmentInstructions) {
        this.assignmentInstructions = assignmentInstructions;
    }

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }

    public List<EvidenceDto> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<EvidenceDto> evidences) {
        this.evidences = evidences;
    }

    public List<IncidentUpdateDto> getUpdates() {
        return updates;
    }

    public void setUpdates(List<IncidentUpdateDto> updates) {
        this.updates = updates;
    }
}
