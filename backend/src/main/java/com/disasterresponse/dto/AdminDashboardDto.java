package com.disasterresponse.dto;

public class AdminDashboardDto {
    private long totalUsers;
    private long totalStudents;
    private long totalOfficers;
    private long totalDisasterTypes;
    private long totalModules;
    private long totalQuizzes;
    private long totalSimulations;
    private long totalDepartments;
    private long totalResponseTeams;
    private long totalReports;
    private long verifiedIncidents;
    private long resolvedIncidents;

    public AdminDashboardDto() {
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public long getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(long totalStudents) {
        this.totalStudents = totalStudents;
    }

    public long getTotalOfficers() {
        return totalOfficers;
    }

    public void setTotalOfficers(long totalOfficers) {
        this.totalOfficers = totalOfficers;
    }

    public long getTotalDisasterTypes() {
        return totalDisasterTypes;
    }

    public void setTotalDisasterTypes(long totalDisasterTypes) {
        this.totalDisasterTypes = totalDisasterTypes;
    }

    public long getTotalModules() {
        return totalModules;
    }

    public void setTotalModules(long totalModules) {
        this.totalModules = totalModules;
    }

    public long getTotalQuizzes() {
        return totalQuizzes;
    }

    public void setTotalQuizzes(long totalQuizzes) {
        this.totalQuizzes = totalQuizzes;
    }

    public long getTotalSimulations() {
        return totalSimulations;
    }

    public void setTotalSimulations(long totalSimulations) {
        this.totalSimulations = totalSimulations;
    }

    public long getTotalDepartments() {
        return totalDepartments;
    }

    public void setTotalDepartments(long totalDepartments) {
        this.totalDepartments = totalDepartments;
    }

    public long getTotalResponseTeams() {
        return totalResponseTeams;
    }

    public void setTotalResponseTeams(long totalResponseTeams) {
        this.totalResponseTeams = totalResponseTeams;
    }

    public long getTotalReports() {
        return totalReports;
    }

    public void setTotalReports(long totalReports) {
        this.totalReports = totalReports;
    }

    public long getVerifiedIncidents() {
        return verifiedIncidents;
    }

    public void setVerifiedIncidents(long verifiedIncidents) {
        this.verifiedIncidents = verifiedIncidents;
    }

    public long getResolvedIncidents() {
        return resolvedIncidents;
    }

    public void setResolvedIncidents(long resolvedIncidents) {
        this.resolvedIncidents = resolvedIncidents;
    }
}
