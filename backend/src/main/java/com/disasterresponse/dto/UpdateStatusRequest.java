package com.disasterresponse.dto;

import com.disasterresponse.enums.ReportStatus;

public class UpdateStatusRequest {

    private ReportStatus status;
    private String remarks;

    public UpdateStatusRequest() {
    }

    public UpdateStatusRequest(ReportStatus status, String remarks) {
        this.status = status;
        this.remarks = remarks;
    }

    public ReportStatus getStatus() {
        return status;
    }

    public void setStatus(ReportStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
