package com.disasterresponse.handler;

import com.disasterresponse.dto.ReportResponseDto;
import com.disasterresponse.service.DisasterReportService;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class MapHandler extends BaseHandler {

    private final DisasterReportService reportService;

    public MapHandler() {
        this.reportService = new DisasterReportService();
    }

    public MapHandler(DisasterReportService reportService) {
        this.reportService = reportService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/map");

        if ("GET".equalsIgnoreCase(method) && ("incidents".equalsIgnoreCase(sub) || sub.isEmpty())) {
            List<ReportResponseDto> incidents = reportService.getMapIncidents();
            sendSuccess(exchange, incidents);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
