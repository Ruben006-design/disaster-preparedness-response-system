package com.disasterresponse.dto;

import com.disasterresponse.enums.SeverityLevel;

public class AssignDepartmentRequest {

    private Long departmentId;
    private Long responseTeamId;
    private SeverityLevel priority = SeverityLevel.HIGH;
    private String instructions;

    public AssignDepartmentRequest() {
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getResponseTeamId() {
        return responseTeamId;
    }

    public void setResponseTeamId(Long responseTeamId) {
        this.responseTeamId = responseTeamId;
    }

    public SeverityLevel getPriority() {
        return priority;
    }

    public void setPriority(SeverityLevel priority) {
        this.priority = priority;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }
}
