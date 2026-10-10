package com.disasterresponse.model;

import java.time.LocalDateTime;

public class SimulationResult {
    private Long id;
    private Long userId;
    private Long simulationId;
    private Simulation simulation;
    private Long selectedOptionId;
    private SimulationOption selectedOption;
    private Integer scoreEarned;
    private LocalDateTime completedAt = LocalDateTime.now();

    public SimulationResult() {
    }

    public SimulationResult(Long userId, Long simulationId, Long selectedOptionId, Integer scoreEarned) {
        this.userId = userId;
        this.simulationId = simulationId;
        this.selectedOptionId = selectedOptionId;
        this.scoreEarned = scoreEarned;
        this.completedAt = LocalDateTime.now();
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

    public Long getSimulationId() {
        return simulationId;
    }

    public void setSimulationId(Long simulationId) {
        this.simulationId = simulationId;
    }

    public Simulation getSimulation() {
        return simulation;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
        if (simulation != null) {
            this.simulationId = simulation.getId();
        }
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public SimulationOption getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(SimulationOption selectedOption) {
        this.selectedOption = selectedOption;
        if (selectedOption != null) {
            this.selectedOptionId = selectedOption.getId();
        }
    }

    public Integer getScoreEarned() {
        return scoreEarned;
    }

    public void setScoreEarned(Integer scoreEarned) {
        this.scoreEarned = scoreEarned;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }
}
