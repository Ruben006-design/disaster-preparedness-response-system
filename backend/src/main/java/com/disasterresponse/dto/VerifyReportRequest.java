package com.disasterresponse.dto;

public class VerifyReportRequest {
    private String verificationNotes;

    public VerifyReportRequest() {
    }

    public VerifyReportRequest(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
}
