package com.disasterresponse.model;

import java.time.LocalDateTime;

public class ModuleProgress {
    private Long id;
    private Long userId;
    private Long moduleId;
    private boolean completed = false;
    private Integer completionPercentage = 0;
    private LocalDateTime lastAccessedAt;
    private LocalDateTime completedAt;

    public ModuleProgress() {
    }

    public ModuleProgress(Long userId, Long moduleId, boolean completed, Integer completionPercentage) {
        this.userId = userId;
        this.moduleId = moduleId;
        this.completed = completed;
        this.completionPercentage = completionPercentage;
        this.lastAccessedAt = LocalDateTime.now();
        if (completed) {
            this.completedAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
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
