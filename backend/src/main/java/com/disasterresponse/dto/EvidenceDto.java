package com.disasterresponse.dto;

import com.disasterresponse.enums.EvidenceCategory;

import java.time.LocalDateTime;

public class EvidenceDto {
    private Long id;
    private Long reportId;
    private String originalFileName;
    private String storedFileName;
    private String fileType;
    private Long fileSize;
    private String fileUrl;
    private EvidenceCategory fileCategory;
    private LocalDateTime uploadedAt;

    public EvidenceDto() {
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
