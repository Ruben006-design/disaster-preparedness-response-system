package com.disasterresponse.dto;

import com.disasterresponse.enums.RoleType;

public class RegisterRequest {

    private String name;
    private String email;
    private String password;
    private String phone;
    private String officerRegistrationCode;
    private RoleType role = RoleType.STUDENT;

    public RegisterRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOfficerRegistrationCode() {
        return officerRegistrationCode;
    }

    public void setOfficerRegistrationCode(String officerRegistrationCode) {
        this.officerRegistrationCode = officerRegistrationCode;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
