package com.disasterresponse.handler;

import com.disasterresponse.dto.QuizResultDto;
import com.disasterresponse.dto.QuizSubmitRequest;
import com.disasterresponse.model.Quiz;
import com.disasterresponse.service.EducationService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;

public class QuizHandler extends BaseHandler {

    private final EducationService educationService;

    public QuizHandler() {
        this.educationService = new EducationService();
    }

    public QuizHandler(EducationService educationService) {
        this.educationService = educationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/quizzes");

        if ("GET".equalsIgnoreCase(method)) {
            if ("results/my".equalsIgnoreCase(sub)) {
                Long userId = getAuthenticatedUserId(exchange);
                List<QuizResultDto> results = educationService.getMyQuizResults(userId);
                sendSuccess(exchange, results);
                return;
            } else if (sub.isEmpty()) {
                List<Quiz> list = educationService.getAllQuizzes();
                sendSuccess(exchange, list);
                return;
            } else {
                Long id = Long.parseLong(sub);
                Quiz q = educationService.getQuizById(id);
                sendSuccess(exchange, q);
                return;
            }
        } else if ("POST".equalsIgnoreCase(method) && sub.endsWith("/submit")) {
            Long userId = getAuthenticatedUserId(exchange);
            String idStr = sub.substring(0, sub.indexOf("/submit"));
            Long quizId = Long.parseLong(idStr);

            String body = HttpUtil.readRequestBody(exchange);
            QuizSubmitRequest req = JsonUtil.fromJson(body, QuizSubmitRequest.class);
            QuizResultDto res = educationService.submitQuiz(userId, quizId, req);
            sendSuccess(exchange, 200, "Quiz submitted successfully.", res);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
