package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.model.IncidentUpdate;
import com.disasterresponse.model.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IncidentUpdateDAO {

    public IncidentUpdate save(IncidentUpdate update) {
        String sql = "INSERT INTO incident_updates (report_id, updated_by_id, previous_status, new_status, remarks, timestamp) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, update.getReportId());
            if (update.getUpdatedById() != null) {
                ps.setLong(2, update.getUpdatedById());
            } else {
                ps.setNull(2, Types.BIGINT);
            }
            ps.setString(3, update.getPreviousStatus());
            ps.setString(4, update.getNewStatus());
            ps.setString(5, update.getRemarks());
            ps.setTimestamp(6, Timestamp.valueOf(update.getTimestamp() != null ? update.getTimestamp() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) update.setId(rs.getLong(1));
            }
            return update;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving incident update: " + e.getMessage(), e);
        }
    }

    public List<IncidentUpdate> findByReportId(Long reportId) {
        String sql = "SELECT u.*, usr.name as updater_name, usr.email as updater_email " +
                "FROM incident_updates u " +
                "LEFT JOIN users usr ON u.updated_by_id = usr.id " +
                "WHERE u.report_id = ? ORDER BY u.timestamp ASC";
        List<IncidentUpdate> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    IncidentUpdate iu = new IncidentUpdate();
                    iu.setId(rs.getLong("id"));
                    iu.setReportId(rs.getLong("report_id"));
                    long updaterId = rs.getLong("updated_by_id");
                    if (!rs.wasNull()) {
                        iu.setUpdatedById(updaterId);
                        User user = new User();
                        user.setId(updaterId);
                        user.setName(rs.getString("updater_name"));
                        user.setEmail(rs.getString("updater_email"));
                        iu.setUpdatedBy(user);
                    }
                    iu.setPreviousStatus(rs.getString("previous_status"));
                    iu.setNewStatus(rs.getString("new_status"));
                    iu.setRemarks(rs.getString("remarks"));
                    Timestamp ts = rs.getTimestamp("timestamp");
                    if (ts != null) iu.setTimestamp(ts.toLocalDateTime());
                    list.add(iu);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching updates for report: " + e.getMessage(), e);
        }
    }
}
