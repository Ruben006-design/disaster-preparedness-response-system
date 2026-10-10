package com.disasterresponse.dto;

public class RejectReportRequest {

    private String rejectionReason;

    public RejectReportRequest() {
    }

    public RejectReportRequest(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }
}
