package com.disasterresponse.model;

import com.disasterresponse.enums.EvidenceCategory;

import java.time.LocalDateTime;

public class Evidence {
    private Long id;
    private Long reportId;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private EvidenceCategory fileCategory = EvidenceCategory.IMAGE;
    private LocalDateTime uploadedAt = LocalDateTime.now();

    public Evidence() {
    }

    public Evidence(Long reportId, String originalFileName, String storedFileName, String fileType, Long fileSize, String fileUrl, EvidenceCategory fileCategory) {
        this.reportId = reportId;
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.fileUrl = fileUrl;
        this.fileCategory = fileCategory != null ? fileCategory : EvidenceCategory.IMAGE;
        this.uploadedAt = LocalDateTime.now();
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

    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getStoredFileName() {
        return storedFileName;
    }

    public void setStoredFileName(String storedFileName) {
        this.storedFileName = storedFileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }

    public EvidenceCategory getFileCategory() {
        return fileCategory;
    }

    public void setFileCategory(EvidenceCategory fileCategory) {
        this.fileCategory = fileCategory;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
