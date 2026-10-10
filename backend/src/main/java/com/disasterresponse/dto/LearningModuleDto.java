package com.disasterresponse.dto;

import com.disasterresponse.enums.SeverityLevel;

import java.time.LocalDateTime;

public class LearningModuleDto {
    private Long id;
    private String title;
    private Long disasterTypeId;
    private String disasterTypeName;
    private String disasterCode;
    private SeverityLevel defaultSeverity;
    private String summary;
    private String content;
    private Integer estimatedMinutes;
    private String mediaUrl;
    private Integer orderIndex;
    private Boolean active;
    private Boolean userCompleted;
    private Integer userProgress;
    private LocalDateTime createdAt;

    public LearningModuleDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public SeverityLevel getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultSeverity(SeverityLevel defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getEstimatedMinutes() {
        return estimatedMinutes;
    }

    public void setEstimatedMinutes(Integer estimatedMinutes) {
        this.estimatedMinutes = estimatedMinutes;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getUserCompleted() {
        return userCompleted;
    }

    public void setUserCompleted(Boolean userCompleted) {
        this.userCompleted = userCompleted;
    }

    public Integer getUserProgress() {
        return userProgress;
    }

    public void setUserProgress(Integer userProgress) {
        this.userProgress = userProgress;
    }

    public void setUserProgressPercentage(Integer p) {
        this.userProgress = p;
    }

    private LocalDateTime lastAccessedAt;

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
