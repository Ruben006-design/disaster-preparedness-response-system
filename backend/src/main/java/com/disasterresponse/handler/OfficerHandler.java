package com.disasterresponse.handler;

import com.disasterresponse.dto.*;
import com.disasterresponse.service.DisasterReportService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class OfficerHandler extends BaseHandler {

    private final DisasterReportService reportService;

    public OfficerHandler() {
        this.reportService = new DisasterReportService();
    }

    public OfficerHandler(DisasterReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long officerId = getAuthenticatedUserId(exchange);
        requireRole(exchange, "GOVERNMENT_OFFICER", "ADMIN");

        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/officer/reports");
        Map<String, String> query = HttpUtil.parseQueryParams(exchange);

        if ("GET".equalsIgnoreCase(method)) {
            if (sub.isEmpty()) {
                String status = query.get("status");
                String severity = query.get("severity");
                String q = query.get("query");
                int page = parseInt(query.get("page"), 0);
                int size = parseInt(query.get("size"), 10);
                PageResponse<ReportResponseDto> reports = reportService.getAllReports(status, severity, q, page, size);
                sendSuccess(exchange, reports);
                return;
            } else {
                Long reportId = Long.parseLong(sub);
                ReportResponseDto report = reportService.getReportById(reportId, officerId, true);
                sendSuccess(exchange, report);
                return;
            }
        } else if ("POST".equalsIgnoreCase(method)) {
            if (sub.endsWith("/verify")) {
                Long reportId = Long.parseLong(sub.substring(0, sub.indexOf("/verify")));
                ReportResponseDto verified = reportService.verifyReport(reportId, officerId);
                sendSuccess(exchange, 200, "Report verified successfully.", verified);
                return;
            } else if (sub.endsWith("/reject")) {
                Long reportId = Long.parseLong(sub.substring(0, sub.indexOf("/reject")));
                String body = HttpUtil.readRequestBody(exchange);
                RejectReportRequest req = JsonUtil.fromJson(body, RejectReportRequest.class);
                ReportResponseDto rejected = reportService.rejectReport(reportId, officerId, req.getRejectionReason());
                sendSuccess(exchange, 200, "Report marked as rejected.", rejected);
                return;
            } else if (sub.endsWith("/assign")) {
                Long reportId = Long.parseLong(sub.substring(0, sub.indexOf("/assign")));
                String body = HttpUtil.readRequestBody(exchange);
                AssignDepartmentRequest req = JsonUtil.fromJson(body, AssignDepartmentRequest.class);
                ReportResponseDto assigned = reportService.assignReport(reportId, req, officerId);
                sendSuccess(exchange, 200, "Incident assigned to response team.", assigned);
                return;
            }
        } else if ("PATCH".equalsIgnoreCase(method)) {
            if (sub.endsWith("/status")) {
                Long reportId = Long.parseLong(sub.substring(0, sub.indexOf("/status")));
                String body = HttpUtil.readRequestBody(exchange);
                UpdateStatusRequest req = JsonUtil.fromJson(body, UpdateStatusRequest.class);
                ReportResponseDto updated = reportService.updateStatus(reportId, req, officerId);
                sendSuccess(exchange, 200, "Status updated to " + req.getStatus(), updated);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }

    private int parseInt(String val, int def) {
        if (val == null || val.trim().isEmpty()) return def;
        try {
            return Integer.parseInt(val.trim());
        } catch (Exception e) {
            return def;
        }
    }
}
