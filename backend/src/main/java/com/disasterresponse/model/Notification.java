package com.disasterresponse.model;

import com.disasterresponse.enums.NotificationType;

import java.time.LocalDateTime;

public class Notification {
    private Long id;
    private Long recipientId;
    private String title;
    private String message;
    private NotificationType type;
    private boolean read = false;
    private String referenceId;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Notification() {
    }

    public Notification(Long recipientId, String title, String message, NotificationType type, String referenceId) {
        this.recipientId = recipientId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.referenceId = referenceId;
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(Long recipientId) {
        this.recipientId = recipientId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
