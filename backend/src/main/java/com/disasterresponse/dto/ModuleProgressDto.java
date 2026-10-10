package com.disasterresponse.dto;

import java.time.LocalDateTime;

public class ModuleProgressDto {
    private Long userId;
    private Long moduleId;
    private String moduleTitle;
    private Boolean completed;
    private Integer completionPercentage;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;

    public ModuleProgressDto() {
    }

    public ModuleProgressDto(Long moduleId, String moduleTitle, Boolean completed, Integer completionPercentage, LocalDateTime completedAt) {
        this.moduleId = moduleId;
        this.moduleTitle = moduleTitle;
        this.completed = completed;
        this.completionPercentage = completionPercentage;
        this.completedAt = completedAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getModuleId() {
        return moduleId;
    }

    public void setModuleId(Long moduleId) {
        this.moduleId = moduleId;
    }

    public String getModuleTitle() {
        return moduleTitle;
    }

    public void setModuleTitle(String moduleTitle) {
        this.moduleTitle = moduleTitle;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public boolean isCompleted() {
        return Boolean.TRUE.equals(completed);
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    public Integer getCompletionPercentage() {
        return completionPercentage;
    }

    public void setCompletionPercentage(Integer completionPercentage) {
        this.completionPercentage = completionPercentage;
    }

    public LocalDateTime getLastAccessedAt() {
        return lastAccessedAt;
    }

    public void setLastAccessedAt(LocalDateTime lastAccessedAt) {
        this.lastAccessedAt = lastAccessedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
