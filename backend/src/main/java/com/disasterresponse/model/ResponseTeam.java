package com.disasterresponse.model;

public class ResponseTeam {
    private Long id;
    private Long departmentId;
    private GovernmentDepartment department;
    private String teamName;
    private String teamLead;
    private String contactPhone;
    private String status = "AVAILABLE";
    private boolean active = true;

    public ResponseTeam() {
    }

    public ResponseTeam(Long departmentId, String teamName, String teamLead, String contactPhone) {
        this.departmentId = departmentId;
        this.teamName = teamName;
        this.teamLead = teamLead;
        this.contactPhone = contactPhone;
        this.status = "AVAILABLE";
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getTeamLead() {
        return teamLead;
    }

    public void setTeamLead(String teamLead) {
        this.teamLead = teamLead;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
