package com.disasterresponse.model;

import java.time.LocalDateTime;

public class LearningModule {
    private Long id;
    private String title;
    private Long disasterTypeId;
    private DisasterType disasterType;
    private String summary;
    private String content;
    private Integer estimatedMinutes = 15;
    private String mediaUrl;
    private Integer orderIndex = 0;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();

    public LearningModule() {
    }

    public LearningModule(String title, Long disasterTypeId, String summary, String content, Integer estimatedMinutes) {
        this.title = title;
        this.disasterTypeId = disasterTypeId;
        this.summary = summary;
        this.content = content;
        this.estimatedMinutes = estimatedMinutes;
        this.active = true;
        this.createdAt = LocalDateTime.now();
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

    public DisasterType getDisasterType() {
        return disasterType;
    }

    public void setDisasterType(DisasterType disasterType) {
        this.disasterType = disasterType;
        if (disasterType != null) {
            this.disasterTypeId = disasterType.getId();
        }
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
