package com.disasterresponse.handler;

import com.disasterresponse.model.DisasterType;
import com.disasterresponse.service.EducationService;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class DisasterHandler extends BaseHandler {

    private final EducationService educationService;

    public DisasterHandler() {
        this.educationService = new EducationService();
    }

    public DisasterHandler(EducationService educationService) {
        this.educationService = educationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/disasters");

        if ("GET".equalsIgnoreCase(method)) {
            if (sub.isEmpty()) {
                List<DisasterType> list = educationService.getAllDisasters();
                sendSuccess(exchange, list);
                return;
            } else {
                Long id = Long.parseLong(sub);
                DisasterType dt = educationService.getDisasterById(id);
                sendSuccess(exchange, dt);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
