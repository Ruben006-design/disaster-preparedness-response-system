package com.disasterresponse.model;

import com.disasterresponse.enums.SeverityLevel;

import java.time.LocalDateTime;

public class ResponseAssignment {
    private Long id;
    private Long reportId;
    private Long departmentId;
    private GovernmentDepartment department;
    private Long responseTeamId;
    private ResponseTeam responseTeam;
    private Long assignedById;
    private User assignedBy;
    private SeverityLevel priority = SeverityLevel.HIGH;
    private String instructions;
    private LocalDateTime assignedAt = LocalDateTime.now();

    public ResponseAssignment() {
    }

    public ResponseAssignment(Long reportId, Long departmentId, Long responseTeamId, Long assignedById, SeverityLevel priority, String instructions) {
        this.reportId = reportId;
        this.departmentId = departmentId;
        this.responseTeamId = responseTeamId;
        this.assignedById = assignedById;
        this.priority = priority != null ? priority : SeverityLevel.HIGH;
        this.instructions = instructions;
        this.assignedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getReportId() {
        return reportId;
    }

    public void setReportId(Long reportId) {
        this.reportId = reportId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public GovernmentDepartment getDepartment() {
        return department;
    }

    public void setDepartment(GovernmentDepartment department) {
        this.department = department;
        if (department != null) {
            this.departmentId = department.getId();
        }
    }

    public Long getResponseTeamId() {
        return responseTeamId;
    }

    public void setResponseTeamId(Long responseTeamId) {
        this.responseTeamId = responseTeamId;
    }

    public ResponseTeam getResponseTeam() {
        return responseTeam;
    }

    public void setResponseTeam(ResponseTeam responseTeam) {
        this.responseTeam = responseTeam;
        if (responseTeam != null) {
            this.responseTeamId = responseTeam.getId();
        }
    }

    public Long getAssignedById() {
        return assignedById;
    }

    public void setAssignedById(Long assignedById) {
        this.assignedById = assignedById;
    }

    public User getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(User assignedBy) {
        this.assignedBy = assignedBy;
        if (assignedBy != null) {
            this.assignedById = assignedBy.getId();
        }
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

    public LocalDateTime getAssignedAt() {
        return assignedAt;
    }

    public void setAssignedAt(LocalDateTime assignedAt) {
        this.assignedAt = assignedAt;
    }
}
