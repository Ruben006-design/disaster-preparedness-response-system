package com.disasterresponse.handler;

import com.disasterresponse.dto.AdminDashboardDto;
import com.disasterresponse.dto.OfficerDashboardDto;
import com.disasterresponse.dto.StudentDashboardDto;
import com.disasterresponse.service.DashboardService;
import com.sun.net.httpserver.HttpExchange;

public class DashboardHandler extends BaseHandler {

    private final DashboardService dashboardService;

    public DashboardHandler() {
        this.dashboardService = new DashboardService();
    }

    public DashboardHandler(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long userId = getAuthenticatedUserId(exchange);
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/dashboard");

        if ("GET".equalsIgnoreCase(method)) {
            if ("student".equalsIgnoreCase(sub)) {
                StudentDashboardDto dto = dashboardService.getStudentDashboard(userId);
                sendSuccess(exchange, dto);
                return;
            } else if ("officer".equalsIgnoreCase(sub)) {
                requireRole(exchange, "GOVERNMENT_OFFICER", "ADMIN");
                OfficerDashboardDto dto = dashboardService.getOfficerDashboard(userId);
                sendSuccess(exchange, dto);
                return;
            } else if ("admin".equalsIgnoreCase(sub)) {
                requireRole(exchange, "ADMIN");
                AdminDashboardDto dto = dashboardService.getAdminDashboard();
                sendSuccess(exchange, dto);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
