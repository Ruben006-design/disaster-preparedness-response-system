package com.disasterresponse.handler;

import com.disasterresponse.dto.CreateReportRequest;
import com.disasterresponse.dto.EvidenceDto;
import com.disasterresponse.dto.PageResponse;
import com.disasterresponse.dto.ReportResponseDto;
import com.disasterresponse.service.DisasterReportService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.disasterresponse.util.MultipartUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class ReportHandler extends BaseHandler {

    private final DisasterReportService reportService;

    public ReportHandler() {
        this.reportService = new DisasterReportService();
    }

    public ReportHandler(DisasterReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long userId = getAuthenticatedUserId(exchange);
        String role = getAuthenticatedUserRole(exchange);
        boolean isOfficerOrAdmin = "GOVERNMENT_OFFICER".equalsIgnoreCase(role) || "ADMIN".equalsIgnoreCase(role);

        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/reports");
        Map<String, String> query = HttpUtil.parseQueryParams(exchange);

        if ("POST".equalsIgnoreCase(method)) {
            if (sub.isEmpty()) {
                String body = HttpUtil.readRequestBody(exchange);
                CreateReportRequest req = JsonUtil.fromJson(body, CreateReportRequest.class);
                ReportResponseDto resp = reportService.submitReport(req, userId);
                sendSuccess(exchange, 201, "Report submitted successfully. Your report ID is: " + resp.getReportCode(), resp);
                return;
            } else if (sub.endsWith("/evidence")) {
                String idStr = sub.substring(0, sub.indexOf("/evidence"));
                Long reportId = Long.parseLong(idStr);
                MultipartUtil.MultipartData multi = MultipartUtil.parse(exchange);
                MultipartUtil.FilePart filePart = multi.getFirstFile();
                if (filePart == null) {
                    throw new IllegalArgumentException("No file found in multipart upload request.");
                }
                EvidenceDto evidence = reportService.uploadEvidence(reportId, filePart, userId);
                sendSuccess(exchange, 201, "Evidence uploaded successfully", evidence);
                return;
            }
        } else if ("GET".equalsIgnoreCase(method)) {
            if ("my".equalsIgnoreCase(sub)) {
                int page = parseInt(query.get("page"), 0);
                int size = parseInt(query.get("size"), 10);
                PageResponse<ReportResponseDto> reports = reportService.getMyReports(userId, page, size);
                sendSuccess(exchange, reports);
                return;
            } else if (sub.isEmpty()) {
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
                ReportResponseDto report = reportService.getReportById(reportId, userId, isOfficerOrAdmin);
                sendSuccess(exchange, report);
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
