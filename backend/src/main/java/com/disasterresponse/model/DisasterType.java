package com.disasterresponse.model;

import com.disasterresponse.enums.SeverityLevel;

public class DisasterType {
    private Long id;
    private String name;
    private String code;
    private String description;
    private String icon;
    private SeverityLevel defaultSeverity = SeverityLevel.MEDIUM;
    private String warningSigns;
    private String safetyProcedures;
    private String doList;
    private String dontList;
    private String beforeSteps;
    private String duringSteps;
    private String afterSteps;
    private boolean active = true;

    public DisasterType() {
    }

    public DisasterType(String name, String code, String description, String icon, SeverityLevel defaultSeverity) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.icon = icon;
        this.defaultSeverity = defaultSeverity;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public SeverityLevel getDefaultSeverity() {
        return defaultSeverity;
    }

    public void setDefaultSeverity(SeverityLevel defaultSeverity) {
        this.defaultSeverity = defaultSeverity;
    }

    public String getWarningSigns() {
        return warningSigns;
    }

    public void setWarningSigns(String warningSigns) {
        this.warningSigns = warningSigns;
    }

    public String getSafetyProcedures() {
        return safetyProcedures;
    }

    public void setSafetyProcedures(String safetyProcedures) {
        this.safetyProcedures = safetyProcedures;
    }

    public String getDoList() {
        return doList;
    }

    public void setDoList(String doList) {
        this.doList = doList;
    }

    public String getDontList() {
        return dontList;
    }

    public void setDontList(String dontList) {
        this.dontList = dontList;
    }

    public String getBeforeSteps() {
        return beforeSteps;
    }

    public void setBeforeSteps(String beforeSteps) {
        this.beforeSteps = beforeSteps;
    }

    public String getDuringSteps() {
        return duringSteps;
    }

    public void setDuringSteps(String duringSteps) {
        this.duringSteps = duringSteps;
    }

    public String getAfterSteps() {
        return afterSteps;
    }

    public void setAfterSteps(String afterSteps) {
        this.afterSteps = afterSteps;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
