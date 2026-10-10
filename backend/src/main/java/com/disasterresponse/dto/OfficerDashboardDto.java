package com.disasterresponse.dto;

import java.util.ArrayList;
import java.util.List;

public class OfficerDashboardDto {
    private long pendingReports;
    private long underReviewReports;
    private long verifiedIncidents;
    private long activeResponses;
    private long resolvedIncidents;
    private long rejectedReports;
    private List<ReportResponseDto> pendingQueue = new ArrayList<>();
    private List<ReportResponseDto> activeResponsesQueue = new ArrayList<>();

    public OfficerDashboardDto() {
    }

    public long getPendingReports() {
        return pendingReports;
    }

    public void setPendingReports(long pendingReports) {
        this.pendingReports = pendingReports;
    }

    public long getUnderReviewReports() {
        return underReviewReports;
    }

    public void setUnderReviewReports(long underReviewReports) {
        this.underReviewReports = underReviewReports;
    }

    public long getVerifiedIncidents() {
        return verifiedIncidents;
    }

    public void setVerifiedIncidents(long verifiedIncidents) {
        this.verifiedIncidents = verifiedIncidents;
    }

    public long getActiveResponses() {
        return activeResponses;
    }

    public void setActiveResponses(long activeResponses) {
        this.activeResponses = activeResponses;
    }

    public long getResolvedIncidents() {
        return resolvedIncidents;
    }

    public void setResolvedIncidents(long resolvedIncidents) {
        this.resolvedIncidents = resolvedIncidents;
    }

    public long getRejectedReports() {
        return rejectedReports;
    }

    public void setRejectedReports(long rejectedReports) {
        this.rejectedReports = rejectedReports;
    }

    public List<ReportResponseDto> getPendingQueue() {
        return pendingQueue;
    }

    public void setPendingQueue(List<ReportResponseDto> pendingQueue) {
        this.pendingQueue = pendingQueue;
    }

    public List<ReportResponseDto> getActiveResponsesQueue() {
        return activeResponsesQueue;
    }

    public void setActiveResponsesQueue(List<ReportResponseDto> activeResponsesQueue) {
        this.activeResponsesQueue = activeResponsesQueue;
    }
}
