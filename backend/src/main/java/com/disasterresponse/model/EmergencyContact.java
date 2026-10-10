package com.disasterresponse.model;

public class EmergencyContact {
    private Long id;
    private String serviceName;
    private String organization;
    private String phoneNumber;
    private String alternatePhone;
    private String location;
    private Integer priorityOrder = 0;
    private boolean active = true;

    public EmergencyContact() {
    }

    public EmergencyContact(String serviceName, String organization, String phoneNumber, String alternatePhone, String location, Integer priorityOrder) {
        this.serviceName = serviceName;
        this.organization = organization;
        this.phoneNumber = phoneNumber;
        this.alternatePhone = alternatePhone;
        this.location = location;
        this.priorityOrder = priorityOrder;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getOrganization() {
        return organization;
    }

    public void setOrganization(String organization) {
        this.organization = organization;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAlternatePhone() {
        return alternatePhone;
    }

    public void setAlternatePhone(String alternatePhone) {
        this.alternatePhone = alternatePhone;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPriorityOrder() {
        return priorityOrder;
    }

    public void setPriorityOrder(Integer priorityOrder) {
        this.priorityOrder = priorityOrder;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
