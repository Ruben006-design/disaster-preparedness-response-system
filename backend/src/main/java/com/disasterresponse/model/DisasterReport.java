package com.disasterresponse.model;

import com.disasterresponse.enums.ReportStatus;
import com.disasterresponse.enums.SeverityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DisasterReport {
    private Long id;
    private String reportCode;
    private Long reporterId;
    private User reporter;
    private Long disasterTypeId;
    private DisasterType disasterType;
    private SeverityLevel severity;
    private ReportStatus status = ReportStatus.SUBMITTED;
    private String title;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String aftermath;
    private String contactPhone;
    private String rejectionReason;
    private Long verifiedById;
    private User verifiedBy;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    private List<Evidence> evidences = new ArrayList<>();
    private List<IncidentUpdate> updates = new ArrayList<>();
    private ResponseAssignment assignment;

    public DisasterReport() {
    }

    public DisasterReport(String reportCode, Long reporterId, Long disasterTypeId, SeverityLevel severity,
                          String title, String description, String address, Double latitude, Double longitude) {
        this.reportCode = reportCode;
        this.reporterId = reporterId;
        this.disasterTypeId = disasterTypeId;
        this.severity = severity;
        this.status = ReportStatus.SUBMITTED;
        this.title = title;
        this.description = description;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
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

    public User getReporter() {
        return reporter;
    }

    public void setReporter(User reporter) {
        this.reporter = reporter;
        if (reporter != null) {
            this.reporterId = reporter.getId();
        }
    }

    public Long getDisasterTypeId() {
        return disasterTypeId;
    }

    public void setDisasterTypeId(Long disasterTypeId) {
        this.disasterTypeId = disasterTypeId;
    }

    public DisasterType getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(DisasterType disasterType) {
        this.disasterType = disasterType;
        if (disasterType != null) {
            this.disasterTypeId = disasterType.getId();
        }
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

    public User getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(User verifiedBy) {
        this.verifiedBy = verifiedBy;
        if (verifiedBy != null) {
            this.verifiedById = verifiedBy.getId();
        }
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

    public List<Evidence> getEvidences() {
        return evidences;
    }

    public void setEvidences(List<Evidence> evidences) {
        this.evidences = evidences;
    }

    public List<IncidentUpdate> getUpdates() {
        return updates;
    }

    public void setUpdates(List<IncidentUpdate> updates) {
        this.updates = updates;
    }

    public ResponseAssignment getAssignment() {
        return assignment;
    }

    public void setAssignment(ResponseAssignment assignment) {
        this.assignment = assignment;
    }
}
