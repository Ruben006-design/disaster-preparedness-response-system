package com.disasterresponse.model;

import java.time.LocalDateTime;

public class IncidentUpdate {
    private Long id;
    private Long reportId;
    private Long updatedById;
    private User updatedBy;
    private String previousStatus;
    private String newStatus;
    private String remarks;
    private LocalDateTime timestamp = LocalDateTime.now();

    public IncidentUpdate() {
    }

    public IncidentUpdate(Long reportId, Long updatedById, String previousStatus, String newStatus, String remarks) {
        this.reportId = reportId;
        this.updatedById = updatedById;
        this.previousStatus = previousStatus;
        this.newStatus = newStatus;
        this.remarks = remarks;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getUpdatedById() {
        return updatedById;
    }

    public void setUpdatedById(Long updatedById) {
        this.updatedById = updatedById;
    }

    public User getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(User updatedBy) {
        this.updatedBy = updatedBy;
        if (updatedBy != null) {
            this.updatedById = updatedBy.getId();
        }
    }

    public String getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(String previousStatus) {
        this.previousStatus = previousStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
