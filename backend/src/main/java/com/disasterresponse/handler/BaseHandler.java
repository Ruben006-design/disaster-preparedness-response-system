package com.disasterresponse.handler;

import com.disasterresponse.dto.ApiResponse;
import com.disasterresponse.util.HttpUtil;
import com.disasterresponse.util.JwtUtil;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;

/**
 * Base HTTP Handler providing common routing, CORS preflight, JWT authentication, and JSON responses.
 */
public abstract class BaseHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            if (HttpUtil.handlePreflight(exchange)) {
                return;
            }
            process(exchange);
        } catch (IllegalArgumentException e) {
            sendError(exchange, 400, e.getMessage());
        } catch (SecurityException e) {
            sendError(exchange, 401, e.getMessage());
        } catch (Exception e) {
            System.err.println("[Handler Error] " + exchange.getRequestURI() + ": " + e.getMessage());
            e.printStackTrace();
            sendError(exchange, 500, "Internal Server Error: " + e.getMessage());
        }
    }

    protected abstract void process(HttpExchange exchange) throws Exception;

    protected Long getAuthenticatedUserId(HttpExchange exchange) {
        String token = HttpUtil.extractBearerToken(exchange);
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new SecurityException("Unauthorized: Valid JWT authentication token is required.");
        }
        return JwtUtil.getUserIdFromToken(token);
    }

    protected Long getOptionalUserId(HttpExchange exchange) {
        String token = HttpUtil.extractBearerToken(exchange);
        if (token != null && JwtUtil.validateToken(token)) {
            return JwtUtil.getUserIdFromToken(token);
        }
        return null;
    }

    protected String getAuthenticatedUserRole(HttpExchange exchange) {
        String token = HttpUtil.extractBearerToken(exchange);
        if (token == null || !JwtUtil.validateToken(token)) {
            throw new SecurityException("Unauthorized: Valid JWT authentication token is required.");
        }
        return JwtUtil.getRoleFromToken(token);
    }

    protected void requireRole(HttpExchange exchange, String... allowedRoles) {
        String role = getAuthenticatedUserRole(exchange);
        for (String allowed : allowedRoles) {
            if (allowed.equalsIgnoreCase(role)) {
                return;
            }
        }
        throw new SecurityException("Forbidden: Insufficient privileges for role " + role);
    }

    protected void sendSuccess(HttpExchange exchange, Object data) throws IOException {
        ApiResponse<Object> resp = ApiResponse.success(data);
        HttpUtil.sendJsonResponse(exchange, 200, resp);
    }

    protected void sendSuccess(HttpExchange exchange, int statusCode, String message, Object data) throws IOException {
        ApiResponse<Object> resp = ApiResponse.success(message, data);
        HttpUtil.sendJsonResponse(exchange, statusCode, resp);
    }

    protected void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        ApiResponse<Object> resp = ApiResponse.error(message);
        HttpUtil.sendJsonResponse(exchange, statusCode, resp);
    }

    protected String getSubPath(HttpExchange exchange, String basePath) {
        String path = exchange.getRequestURI().getPath();
        if (path.startsWith(basePath)) {
            String sub = path.substring(basePath.length());
            return sub.startsWith("/") ? sub.substring(1) : sub;
        }
        return "";
    }
}
