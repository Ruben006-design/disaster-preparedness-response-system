package com.disasterresponse.handler;

import com.disasterresponse.dto.PreparednessScoreDto;
import com.disasterresponse.service.EducationService;
import com.sun.net.httpserver.HttpExchange;

public class PreparednessHandler extends BaseHandler {

    private final EducationService educationService;

    public PreparednessHandler() {
        this.educationService = new EducationService();
    }

    public PreparednessHandler(EducationService educationService) {
        this.educationService = educationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long userId = getAuthenticatedUserId(exchange);
        String method = exchange.getRequestMethod();

        if ("GET".equalsIgnoreCase(method)) {
            PreparednessScoreDto score = educationService.calculatePreparednessScore(userId);
            sendSuccess(exchange, score);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
