package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.NotificationType;
import com.disasterresponse.model.Notification;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {

    public Notification save(Notification notif) {
        String sql = "INSERT INTO notifications (recipient_id, title, message, type, is_read, reference_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, notif.getRecipientId());
            ps.setString(2, notif.getTitle());
            ps.setString(3, notif.getMessage());
            ps.setString(4, notif.getType().name());
            ps.setBoolean(5, notif.isRead());
            ps.setString(6, notif.getReferenceId());
            ps.setTimestamp(7, Timestamp.valueOf(notif.getCreatedAt() != null ? notif.getCreatedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) notif.setId(rs.getLong(1));
            }
            return notif;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving notification: " + e.getMessage(), e);
        }
    }

    public List<Notification> findByRecipientId(Long recipientId, int limit) {
        String sql = "SELECT * FROM notifications WHERE recipient_id = ? ORDER BY created_at DESC LIMIT ?";
        List<Notification> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recipientId);
            ps.setInt(2, limit > 0 ? limit : 20);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapNotif(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching notifications: " + e.getMessage(), e);
        }
    }

    public long countUnreadByRecipientId(Long recipientId) {
        String sql = "SELECT COUNT(*) FROM notifications WHERE recipient_id = ? AND is_read = false";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recipientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting unread notifications: " + e.getMessage(), e);
        }
    }

    public boolean markAsRead(Long id, Long recipientId) {
        String sql = "UPDATE notifications SET is_read = true WHERE id = ? AND recipient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.setLong(2, recipientId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error marking notification as read: " + e.getMessage(), e);
        }
    }

    public int markAllAsRead(Long recipientId) {
        String sql = "UPDATE notifications SET is_read = true WHERE recipient_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, recipientId);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error marking all notifications as read: " + e.getMessage(), e);
        }
    }

    private Notification mapNotif(ResultSet rs) throws SQLException {
        Notification n = new Notification();
        n.setId(rs.getLong("id"));
        n.setRecipientId(rs.getLong("recipient_id"));
        n.setTitle(rs.getString("title"));
        n.setMessage(rs.getString("message"));
        n.setType(NotificationType.valueOf(rs.getString("type")));
        n.setRead(rs.getBoolean("is_read"));
        n.setReferenceId(rs.getString("reference_id"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) n.setCreatedAt(cr.toLocalDateTime());
        return n;
    }
}
