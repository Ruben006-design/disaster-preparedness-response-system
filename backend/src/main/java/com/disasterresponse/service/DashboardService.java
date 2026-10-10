package com.disasterresponse.service;

import com.disasterresponse.dao.*;
import com.disasterresponse.dto.*;
import com.disasterresponse.enums.ReportStatus;
import com.disasterresponse.model.DisasterReport;

import java.util.List;
import java.util.stream.Collectors;

public class DashboardService {

    private final UserDAO userDAO;
    private final DisasterDAO disasterDAO;
    private final LearningModuleDAO moduleDAO;
    private final QuizDAO quizDAO;
    private final SimulationDAO simulationDAO;
    private final DepartmentDAO departmentDAO;
    private final ResponseTeamDAO teamDAO;
    private final DisasterReportDAO reportDAO;
    private final DisasterReportService reportService;
    private final EducationService educationService;

    public DashboardService() {
        this.userDAO = new UserDAO();
        this.disasterDAO = new DisasterDAO();
        this.moduleDAO = new LearningModuleDAO();
        this.quizDAO = new QuizDAO();
        this.simulationDAO = new SimulationDAO();
        this.departmentDAO = new DepartmentDAO();
        this.teamDAO = new ResponseTeamDAO();
        this.reportDAO = new DisasterReportDAO();
        this.reportService = new DisasterReportService();
        this.educationService = new EducationService();
    }

    public StudentDashboardDto getStudentDashboard(Long userId) {
        StudentDashboardDto dto = new StudentDashboardDto();

        PreparednessScoreDto prep = educationService.calculatePreparednessScore(userId);
        dto.setPreparednessScore(prep);
        dto.setModulesCompleted(prep.getModulesCompleted());
        dto.setTotalModules(prep.getTotalModules());
        dto.setQuizAverage(prep.getAverageQuizScore());
        dto.setSimulationsCompleted(prep.getSimulationsCompleted());
        dto.setTotalSimulations(prep.getTotalSimulations());

        PageResponse<ReportResponseDto> myReports = reportService.getMyReports(userId, 0, 5);
        dto.setRecentReports(myReports.getContent());

        List<ReportResponseDto> mapIncidents = reportService.getMapIncidents();
        dto.setActiveVerifiedDisasters(mapIncidents.stream().limit(5).collect(Collectors.toList()));

        return dto;
    }

    public OfficerDashboardDto getOfficerDashboard(Long officerId) {
        OfficerDashboardDto dto = new OfficerDashboardDto();

        dto.setPendingReports(reportDAO.countByStatus(ReportStatus.SUBMITTED));
        dto.setUnderReviewReports(reportDAO.countByStatus(ReportStatus.UNDER_REVIEW));
        dto.setVerifiedIncidents(reportDAO.countByStatus(ReportStatus.VERIFIED));
        dto.setActiveResponses(reportDAO.countByStatus(ReportStatus.ASSIGNED) + reportDAO.countByStatus(ReportStatus.RESPONSE_IN_PROGRESS));
        dto.setResolvedIncidents(reportDAO.countByStatus(ReportStatus.RESOLVED));
        dto.setRejectedReports(reportDAO.countByStatus(ReportStatus.REJECTED));

        PageResponse<ReportResponseDto> pending = reportService.getAllReports("SUBMITTED", null, null, 0, 10);
        dto.setPendingQueue(pending.getContent());

        PageResponse<ReportResponseDto> active = reportService.getAllReports("ASSIGNED", null, null, 0, 10);
        dto.setActiveResponsesQueue(active.getContent());

        return dto;
    }

    public AdminDashboardDto getAdminDashboard() {
        AdminDashboardDto dto = new AdminDashboardDto();

        dto.setTotalUsers(userDAO.countAll());
        dto.setTotalStudents(userDAO.countByRole(com.disasterresponse.enums.RoleType.STUDENT));
        dto.setTotalOfficers(userDAO.countByRole(com.disasterresponse.enums.RoleType.GOVERNMENT_OFFICER));
        dto.setTotalDisasterTypes(disasterDAO.countTotal());
        dto.setTotalModules(moduleDAO.countTotal());
        dto.setTotalQuizzes(quizDAO.countTotal());
        dto.setTotalSimulations(simulationDAO.countTotal());
        dto.setTotalDepartments(departmentDAO.countTotal());
        dto.setTotalResponseTeams(teamDAO.countTotal());
        dto.setTotalReports(reportDAO.countTotal());
        dto.setVerifiedIncidents(reportDAO.countByStatus(ReportStatus.VERIFIED));
        dto.setResolvedIncidents(reportDAO.countByStatus(ReportStatus.RESOLVED));

        return dto;
    }
}
