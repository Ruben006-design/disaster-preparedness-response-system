package com.disasterresponse.handler;

import com.disasterresponse.dto.ChangePasswordRequest;
import com.disasterresponse.dto.UpdateProfileRequest;
import com.disasterresponse.dto.UserProfileResponse;
import com.disasterresponse.service.UserService;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JsonUtil;
import com.sun.net.httpserver.HttpExchange;

public class UserHandler extends BaseHandler {

    private final UserService userService;

    public UserHandler() {
        this.userService = new UserService();
    }

    public UserHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long userId = getAuthenticatedUserId(exchange);
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        if (path.endsWith("/me/change-password") && "PUT".equalsIgnoreCase(method)) {
            String body = HttpUtil.readRequestBody(exchange);
            ChangePasswordRequest req = JsonUtil.fromJson(body, ChangePasswordRequest.class);
            userService.changePassword(userId, req);
            sendSuccess(exchange, 200, "Password changed successfully.", null);
            return;
        }

        if (path.endsWith("/me")) {
            if ("GET".equalsIgnoreCase(method)) {
                UserProfileResponse profile = userService.getProfile(userId);
                sendSuccess(exchange, profile);
                return;
            } else if ("PUT".equalsIgnoreCase(method)) {
                String body = HttpUtil.readRequestBody(exchange);
                UpdateProfileRequest req = JsonUtil.fromJson(body, UpdateProfileRequest.class);
                UserProfileResponse profile = userService.updateProfile(userId, req);
                sendSuccess(exchange, 200, "Profile updated successfully.", profile);
                return;
            }
        }

        sendError(exchange, 404, "Endpoint not found: " + path);
    }
}
