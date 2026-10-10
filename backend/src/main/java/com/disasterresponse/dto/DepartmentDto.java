package com.disasterresponse.dto;

import com.disasterresponse.enums.DepartmentType;

import java.util.ArrayList;
import java.util.List;

public class DepartmentDto {
    private Long id;
    private String name;
    private String code;
    private DepartmentType departmentType;
    private String description;
    private String contactEmail;
    private String contactPhone;
    private String headName;
    private Boolean active;
    private List<ResponseTeamDto> responseTeams = new ArrayList<>();

    public DepartmentDto() {
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public List<ResponseTeamDto> getResponseTeams() {
        return responseTeams;
    }

    public void setResponseTeams(List<ResponseTeamDto> responseTeams) {
        this.responseTeams = responseTeams;
    }
}
