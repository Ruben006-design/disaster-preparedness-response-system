package com.disasterresponse.dto;

import com.disasterresponse.enums.SeverityLevel;

public class CreateReportRequest {

    private Long disasterTypeId;
    private SeverityLevel severity;
    private String title;
    private String description;
    private String address;
    private Double latitude;
    private Double longitude;
    private String aftermath;
    private String contactPhone;

    public CreateReportRequest() {
    }

    public Long getDisasterTypeId() {
        return disasterTypeId;
    }

    public void setDisasterTypeId(Long disasterTypeId) {
        this.disasterTypeId = disasterTypeId;
    }

    public SeverityLevel getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityLevel severity) {
        this.severity = severity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getAftermath() {
        return aftermath;
    }

    public void setAftermath(String aftermath) {
        this.aftermath = aftermath;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
}
