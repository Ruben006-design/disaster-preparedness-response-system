package com.disasterresponse.handler;

import com.disasterresponse.model.Notification;
import com.disasterresponse.service.NotificationService;
import com.disasterresponse.util.HttpUtil;
import com.sun.net.httpserver.HttpExchange;

import java.util.List;
import java.util.Map;

public class NotificationHandler extends BaseHandler {

    private final NotificationService notificationService;

    public NotificationHandler() {
        this.notificationService = new NotificationService();
    }

    public NotificationHandler(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Override
    protected void process(HttpExchange exchange) throws Exception {
        Long userId = getAuthenticatedUserId(exchange);
        String method = exchange.getRequestMethod();
        String sub = getSubPath(exchange, "/api/notifications");

        if ("GET".equalsIgnoreCase(method)) {
            if ("recent".equalsIgnoreCase(sub)) {
                List<Notification> list = notificationService.getRecentNotifications(userId, 20);
                sendSuccess(exchange, list);
                return;
            } else if ("unread-count".equalsIgnoreCase(sub)) {
                long count = notificationService.getUnreadCount(userId);
                sendSuccess(exchange, count);
                return;
            }
        } else if ("PATCH".equalsIgnoreCase(method) && sub.endsWith("/read")) {
            String idStr = sub.substring(0, sub.indexOf("/read"));
            Long notifId = Long.parseLong(idStr);
            notificationService.markAsRead(notifId, userId);
            sendSuccess(exchange, 200, "Notification marked as read.", null);
            return;
        } else if ("POST".equalsIgnoreCase(method) && "mark-all-read".equalsIgnoreCase(sub)) {
            notificationService.markAllAsRead(userId);
            sendSuccess(exchange, 200, "All notifications marked as read.", null);
            return;
        }

        sendError(exchange, 404, "Endpoint not found: " + exchange.getRequestURI().getPath());
    }
}
