package com.disasterresponse.handler;

import com.disasterresponse.dto.SimulationResultDto;
import com.disasterresponse.dto.SimulationSubmitRequest;
import com.disasterresponse.model.Simulation;
import com.disasterresponse.service.EducationService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class SimulationHandler extends BaseHandler {

    private final EducationService educationService;

    public SimulationHandler() {
        this.educationService = new EducationService();
    }

    public SimulationHandler(EducationService educationService) {
        this.educationService = educationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/simulations");

        if ("GET".equalsIgnoreCase(method)) {
            if ("results/my".equalsIgnoreCase(sub)) {
                Long userId = getAuthenticatedUserId(exchange);
                List<SimulationResultDto> results = educationService.getMySimulationResults(userId);
                sendSuccess(exchange, results);
                return;
            } else if (sub.isEmpty()) {
                List<Simulation> list = educationService.getAllSimulations();
                sendSuccess(exchange, list);
                return;
            } else {
                Long id = Long.parseLong(sub);
                Simulation s = educationService.getSimulationById(id);
                sendSuccess(exchange, s);
                return;
            }
        } else if ("POST".equalsIgnoreCase(method) && sub.endsWith("/submit")) {
            Long userId = getAuthenticatedUserId(exchange);
            String idStr = sub.substring(0, sub.indexOf("/submit"));
            Long simId = Long.parseLong(idStr);

            String body = HttpUtil.readRequestBody(exchange);
            SimulationSubmitRequest req = JsonUtil.fromJson(body, SimulationSubmitRequest.class);
            SimulationResultDto res = educationService.submitSimulation(userId, simId, req);
            sendSuccess(exchange, 200, "Simulation action submitted.", res);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
