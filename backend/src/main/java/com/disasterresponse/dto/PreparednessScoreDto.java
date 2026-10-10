package com.disasterresponse.dto;

public class PreparednessScoreDto {
    private int overallScore; // 0 - 100
    private int moduleCompletionScore; // 0 - 100 (30% weight)
    private int quizPerformanceScore; // 0 - 100 (40% weight)
    private int simulationPerformanceScore; // 0 - 100 (30% weight)
    private long modulesCompleted;
    private long totalModules;
    private double averageQuizScore;
    private long simulationsCompleted;
    private long totalSimulations;
    private String preparednessLevel; // e.g. "BEGINNER", "PREPARED", "READINESS CHAMPION"
    private String transparencyDisclaimer;

    public PreparednessScoreDto() {
        this.transparencyDisclaimer = "This preparedness score is an educational readiness index based on your module completions, quiz evaluations, and interactive simulations. It is designed for self-assessment and training awareness and does not represent an official government-issued certification.";
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public int getModuleCompletionScore() {
        return moduleCompletionScore;
    }

    public void setModuleCompletionScore(int moduleCompletionScore) {
        this.moduleCompletionScore = moduleCompletionScore;
    }

    public int getQuizPerformanceScore() {
        return quizPerformanceScore;
    }

    public void setQuizPerformanceScore(int quizPerformanceScore) {
        this.quizPerformanceScore = quizPerformanceScore;
    }

    public int getSimulationPerformanceScore() {
        return simulationPerformanceScore;
    }

    public void setSimulationPerformanceScore(int simulationPerformanceScore) {
        this.simulationPerformanceScore = simulationPerformanceScore;
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

    public double getAverageQuizScore() {
        return averageQuizScore;
    }

    public void setAverageQuizScore(double averageQuizScore) {
        this.averageQuizScore = averageQuizScore;
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

    public String getPreparednessLevel() {
        return preparednessLevel;
    }

    public void setPreparednessLevel(String preparednessLevel) {
        this.preparednessLevel = preparednessLevel;
    }

    public String getTransparencyDisclaimer() {
        return transparencyDisclaimer;
    }

    public void setTransparencyDisclaimer(String transparencyDisclaimer) {
        this.transparencyDisclaimer = transparencyDisclaimer;
    }
}
