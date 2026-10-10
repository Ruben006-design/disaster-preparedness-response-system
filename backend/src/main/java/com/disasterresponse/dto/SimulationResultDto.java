package com.disasterresponse.dto;

import java.time.LocalDateTime;

public class SimulationResultDto {
    private Long id;
    private Long simulationId;
    private String simulationTitle;
    private Long selectedOptionId;
    private String actionText;
    private String consequenceText;
    private Boolean appropriate;
    private Integer scoreEarned;
    private String safetyFeedback;
    private LocalDateTime completedAt;

    public SimulationResultDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    public String getSimulationTitle() {
        return simulationTitle;
    }

    public void setSimulationTitle(String simulationTitle) {
        this.simulationTitle = simulationTitle;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getActionText() {
        return actionText;
    }

    public void setActionText(String actionText) {
        this.actionText = actionText;
    }

    public String getConsequenceText() {
        return consequenceText;
    }

    public void setConsequenceText(String consequenceText) {
        this.consequenceText = consequenceText;
    }

    public Boolean getAppropriate() {
        return appropriate;
    }

    public void setAppropriate(Boolean appropriate) {
        this.appropriate = appropriate;
    }

    public Integer getScoreEarned() {
        return scoreEarned;
    }

    public void setScoreEarned(Integer scoreEarned) {
        this.scoreEarned = scoreEarned;
    }

    public String getSafetyFeedback() {
        return safetyFeedback;
    }

    public void setSafetyFeedback(String safetyFeedback) {
        this.safetyFeedback = safetyFeedback;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
