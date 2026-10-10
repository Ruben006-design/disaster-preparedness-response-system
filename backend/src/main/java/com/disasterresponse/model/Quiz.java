package com.disasterresponse.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Quiz {
    private Long id;
    private String title;
    private Long disasterTypeId;
    private DisasterType disasterType;
    private String description;
    private Integer passingScore = 70;
    private Integer timeLimitMinutes = 10;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private List<QuizQuestion> questions = new ArrayList<>();

    public Quiz() {
    }

    public Quiz(String title, Long disasterTypeId, String description, Integer passingScore, Integer timeLimitMinutes) {
        this.title = title;
        this.disasterTypeId = disasterTypeId;
        this.description = description;
        this.passingScore = passingScore;
        this.timeLimitMinutes = timeLimitMinutes;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPassingScore() {
        return passingScore;
    }

    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore;
    }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes;
    }

    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes;
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

    public List<QuizQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<QuizQuestion> questions) {
        this.questions = questions;
    }
}
