package com.disasterresponse.handler;

import com.disasterresponse.dto.LearningModuleDto;
import com.disasterresponse.dto.ModuleProgressDto;
import com.disasterresponse.service.EducationService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class ModuleHandler extends BaseHandler {

    private final EducationService educationService;

    public ModuleHandler() {
        this.educationService = new EducationService();
    }

    public ModuleHandler(EducationService educationService) {
        this.educationService = educationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/modules");
        Long userId = getOptionalUserId(exchange);

        if ("GET".equalsIgnoreCase(method)) {
            if (sub.isEmpty()) {
                List<LearningModuleDto> list = educationService.getAllModules(userId);
                sendSuccess(exchange, list);
                return;
            } else {
                Long id = Long.parseLong(sub);
                LearningModuleDto dto = educationService.getModuleById(id, userId);
                sendSuccess(exchange, dto);
                return;
            }
        } else if ("POST".equalsIgnoreCase(method) && sub.endsWith("/progress")) {
            Long authUserId = getAuthenticatedUserId(exchange);
            String idStr = sub.substring(0, sub.indexOf("/progress"));
            Long moduleId = Long.parseLong(idStr);

            String body = HttpUtil.readRequestBody(exchange);
            ModuleProgressDto progressDto = JsonUtil.fromJson(body, ModuleProgressDto.class);
            ModuleProgressDto saved = educationService.updateProgress(authUserId, moduleId, progressDto);
            sendSuccess(exchange, 200, "Progress recorded.", saved);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
