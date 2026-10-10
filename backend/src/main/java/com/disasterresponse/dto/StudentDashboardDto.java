package com.disasterresponse.dto;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardDto {
    private PreparednessScoreDto preparednessScore;
    private long modulesCompleted;
    private long totalModules;
    private double quizAverage;
    private long simulationsCompleted;
    private long totalSimulations;
    private List<ReportResponseDto> recentReports = new ArrayList<>();
    private List<ReportResponseDto> activeVerifiedDisasters = new ArrayList<>();

    public StudentDashboardDto() {
    }

    public PreparednessScoreDto getPreparednessScore() {
        return preparednessScore;
    }

    public void setPreparednessScore(PreparednessScoreDto preparednessScore) {
        this.preparednessScore = preparednessScore;
    }

    public long getModulesCompleted() {
        return modulesCompleted;
    }

    public void setModulesCompleted(long modulesCompleted) {
        this.modulesCompleted = modulesCompleted;
    }

    public long getTotalModules() {
        return totalModules;
    }

    public void setTotalModules(long totalModules) {
        this.totalModules = totalModules;
    }

    public double getQuizAverage() {
        return quizAverage;
    }

    public void setQuizAverage(double quizAverage) {
        this.quizAverage = quizAverage;
    }

    public long getSimulationsCompleted() {
        return simulationsCompleted;
    }

    public void setSimulationsCompleted(long simulationsCompleted) {
        this.simulationsCompleted = simulationsCompleted;
    }

    public long getTotalSimulations() {
        return totalSimulations;
    }

    public void setTotalSimulations(long totalSimulations) {
        this.totalSimulations = totalSimulations;
    }

    public List<ReportResponseDto> getRecentReports() {
        return recentReports;
    }

    public void setRecentReports(List<ReportResponseDto> recentReports) {
        this.recentReports = recentReports;
    }

    public List<ReportResponseDto> getActiveVerifiedDisasters() {
        return activeVerifiedDisasters;
    }

    public void setActiveVerifiedDisasters(List<ReportResponseDto> activeVerifiedDisasters) {
        this.activeVerifiedDisasters = activeVerifiedDisasters;
    }
}
