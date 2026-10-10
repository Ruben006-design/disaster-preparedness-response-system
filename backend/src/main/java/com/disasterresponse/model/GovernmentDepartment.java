package com.disasterresponse.model;

import com.disasterresponse.enums.DepartmentType;

import java.util.ArrayList;
import java.util.List;

public class GovernmentDepartment {
    private Long id;
    private String name;
    private String code;
    private DepartmentType departmentType;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String headName;
    private boolean active = true;
    private List<ResponseTeam> responseTeams = new ArrayList<>();

    public GovernmentDepartment() {
    }

    public GovernmentDepartment(String name, String code, DepartmentType departmentType, String contactEmail, String contactPhone) {
        this.name = name;
        this.code = code;
        this.departmentType = departmentType;
        this.contactEmail = contactEmail;
        this.contactPhone = contactPhone;
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

    public DepartmentType getDepartmentType() {
        return departmentType;
    }

    public void setDepartmentType(DepartmentType departmentType) {
        this.departmentType = departmentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getHeadName() {
        return headName;
    }

    public void setHeadName(String headName) {
        this.headName = headName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<ResponseTeam> getResponseTeams() {
        return responseTeams;
    }

    public void setResponseTeams(List<ResponseTeam> responseTeams) {
        this.responseTeams = responseTeams;
    }
}
