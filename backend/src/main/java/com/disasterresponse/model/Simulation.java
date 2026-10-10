package com.disasterresponse.model;

import com.disasterresponse.enums.SeverityLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Simulation {
    private Long id;
    private String title;
    private Long disasterTypeId;
    private DisasterType disasterType;
    private String scenarioText;
    private String locationContext;
    private SeverityLevel initialRiskLevel = SeverityLevel.HIGH;
    private boolean active = true;
    private LocalDateTime createdAt = LocalDateTime.now();
    private List<SimulationOption> options = new ArrayList<>();

    public Simulation() {
    }

    public Simulation(String title, Long disasterTypeId, String scenarioText, String locationContext, SeverityLevel initialRiskLevel) {
        this.title = title;
        this.disasterTypeId = disasterTypeId;
        this.scenarioText = scenarioText;
        this.locationContext = locationContext;
        this.initialRiskLevel = initialRiskLevel;
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

    public String getScenarioText() {
        return scenarioText;
    }

    public void setScenarioText(String scenarioText) {
        this.scenarioText = scenarioText;
    }

    public String getLocationContext() {
        return locationContext;
    }

    public void setLocationContext(String locationContext) {
        this.locationContext = locationContext;
    }

    public SeverityLevel getInitialRiskLevel() {
        return initialRiskLevel;
    }

    public void setInitialRiskLevel(SeverityLevel initialRiskLevel) {
        this.initialRiskLevel = initialRiskLevel;
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

    public List<SimulationOption> getOptions() {
        return options;
    }

    public void setOptions(List<SimulationOption> options) {
        this.options = options;
    }
}
