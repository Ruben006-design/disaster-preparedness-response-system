package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.EvidenceCategory;
import com.disasterresponse.model.Evidence;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EvidenceDAO {

    public Evidence save(Evidence evidence) {
        String sql = "INSERT INTO evidences (report_id, original_file_name, stored_file_name, file_type, file_size, file_url, file_category, uploaded_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, evidence.getReportId());
            ps.setString(2, evidence.getOriginalFileName());
            ps.setString(3, evidence.getStoredFileName());
            ps.setString(4, evidence.getFileType());
            ps.setLong(5, evidence.getFileSize());
            ps.setString(6, evidence.getFileUrl());
            ps.setString(7, evidence.getFileCategory() != null ? evidence.getFileCategory().name() : EvidenceCategory.IMAGE.name());
            ps.setTimestamp(8, Timestamp.valueOf(evidence.getUploadedAt() != null ? evidence.getUploadedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) evidence.setId(rs.getLong(1));
            }
            return evidence;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving evidence: " + e.getMessage(), e);
        }
    }

    public List<Evidence> findByReportId(Long reportId) {
        String sql = "SELECT * FROM evidences WHERE report_id = ? ORDER BY uploaded_at ASC";
        List<Evidence> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("id"));
                    ev.setReportId(rs.getLong("report_id"));
                    ev.setOriginalFileName(rs.getString("original_file_name"));
                    ev.setStoredFileName(rs.getString("stored_file_name"));
                    ev.setFileType(rs.getString("file_type"));
                    ev.setFileSize(rs.getLong("file_size"));
                    ev.setFileUrl(rs.getString("file_url"));
                    String cat = rs.getString("file_category");
                    if (cat != null) ev.setFileCategory(EvidenceCategory.valueOf(cat));
                    Timestamp up = rs.getTimestamp("uploaded_at");
                    if (up != null) ev.setUploadedAt(up.toLocalDateTime());
                    list.add(ev);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching evidences for report: " + e.getMessage(), e);
        }
    }

    public Evidence findByStoredFileName(String storedFileName) {
        String sql = "SELECT * FROM evidences WHERE stored_file_name = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, storedFileName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Evidence ev = new Evidence();
                    ev.setId(rs.getLong("id"));
                    ev.setReportId(rs.getLong("report_id"));
                    ev.setOriginalFileName(rs.getString("original_file_name"));
                    ev.setStoredFileName(rs.getString("stored_file_name"));
                    ev.setFileType(rs.getString("file_type"));
                    ev.setFileSize(rs.getLong("file_size"));
                    ev.setFileUrl(rs.getString("file_url"));
                    String cat = rs.getString("file_category");
                    if (cat != null) ev.setFileCategory(EvidenceCategory.valueOf(cat));
                    Timestamp up = rs.getTimestamp("uploaded_at");
                    if (up != null) ev.setUploadedAt(up.toLocalDateTime());
                    return ev;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding evidence by stored file name: " + e.getMessage(), e);
        }
    }
}
