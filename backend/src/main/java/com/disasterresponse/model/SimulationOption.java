package com.disasterresponse.model;

public class SimulationOption {
    private Long id;
    private Long simulationId;
    private String actionText;
    private String consequenceText;
    private Integer scoreDelta = 10;
    private boolean appropriate;
    private String safetyFeedback;

    public SimulationOption() {
    }

    public SimulationOption(Long simulationId, String actionText, String consequenceText, Integer scoreDelta, boolean appropriate, String safetyFeedback) {
        this.simulationId = simulationId;
        this.actionText = actionText;
        this.consequenceText = consequenceText;
        this.scoreDelta = scoreDelta;
        this.appropriate = appropriate;
        this.safetyFeedback = safetyFeedback;
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

    public Integer getScoreDelta() {
        return scoreDelta;
    }

    public void setScoreDelta(Integer scoreDelta) {
        this.scoreDelta = scoreDelta;
    }

    public boolean isAppropriate() {
        return appropriate;
    }

    public void setAppropriate(boolean appropriate) {
        this.appropriate = appropriate;
    }

    public String getSafetyFeedback() {
        return safetyFeedback;
    }

    public void setSafetyFeedback(String safetyFeedback) {
        this.safetyFeedback = safetyFeedback;
    }
}
