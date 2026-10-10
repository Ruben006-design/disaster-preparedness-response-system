package com.disasterresponse.handler;

import com.disasterresponse.dto.AuthResponse;
import com.disasterresponse.dto.LoginRequest;
import com.disasterresponse.dto.RegisterRequest;
import com.disasterresponse.service.AuthService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public class AuthHandler extends BaseHandler {

    private final AuthService authService;

    public AuthHandler() {
        this.authService = new AuthService();
    }

    public AuthHandler(AuthService authService) {
        this.authService = authService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if ("POST".equalsIgnoreCase(method)) {
            if (path.endsWith("/register")) {
                String body = HttpUtil.readRequestBody(exchange);
                RegisterRequest req = JsonUtil.fromJson(body, RegisterRequest.class);
                AuthResponse auth = authService.register(req);
                sendSuccess(exchange, 201, "User registered successfully.", auth);
                return;
            } else if (path.endsWith("/login")) {
                String body = HttpUtil.readRequestBody(exchange);
                LoginRequest req = JsonUtil.fromJson(body, LoginRequest.class);
                AuthResponse auth = authService.login(req);
                sendSuccess(exchange, 200, "Login successful.", auth);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + path);
    }
}
