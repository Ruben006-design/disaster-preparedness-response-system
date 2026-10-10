package com.disasterresponse.service;

import com.disasterresponse.dao.NotificationDAO;
import com.disasterresponse.enums.NotificationType;
import com.disasterresponse.model.Notification;

import java.util.List;

public class NotificationService {

    private final NotificationDAO notificationDAO;

    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }

    public NotificationService(NotificationDAO notificationDAO) {
        this.notificationDAO = notificationDAO;
    }

    public Notification createNotification(Long recipientId, String title, String message, NotificationType type, String refId) {
        Notification notif = new Notification(recipientId, title, message, type, refId);
        return notificationDAO.save(notif);
    }

    public List<Notification> getRecentNotifications(Long recipientId, int limit) {
        return notificationDAO.findByRecipientId(recipientId, limit > 0 ? limit : 15);
    }

    public long getUnreadCount(Long recipientId) {
        return notificationDAO.countUnreadByRecipientId(recipientId);
    }

    public boolean markAsRead(Long id, Long recipientId) {
        return notificationDAO.markAsRead(id, recipientId);
    }

    public int markAllAsRead(Long recipientId) {
        return notificationDAO.markAllAsRead(recipientId);
    }
}
