package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.ReportStatus;
import com.disasterresponse.enums.RoleType;
import com.disasterresponse.enums.SeverityLevel;
import com.disasterresponse.model.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class DisasterReportDAO {

    public DisasterReport save(DisasterReport report) {
        String sql = "INSERT INTO disaster_reports (report_code, reporter_id, disaster_type_id, severity, status, title, description, address, latitude, longitude, contact_phone, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, report.getReportCode());
            ps.setLong(2, report.getReporterId());
            ps.setLong(3, report.getDisasterTypeId());
            ps.setString(4, report.getSeverity().name());
            ps.setString(5, report.getStatus() != null ? report.getStatus().name() : ReportStatus.SUBMITTED.name());
            ps.setString(6, report.getTitle());
            ps.setString(7, report.getDescription());
            ps.setString(8, report.getAddress());
            ps.setDouble(9, report.getLatitude());
            ps.setDouble(10, report.getLongitude());
            ps.setString(11, report.getContactPhone());
            ps.setTimestamp(12, Timestamp.valueOf(report.getCreatedAt() != null ? report.getCreatedAt() : LocalDateTime.now()));
            ps.setTimestamp(13, Timestamp.valueOf(report.getUpdatedAt() != null ? report.getUpdatedAt() : LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) report.setId(rs.getLong(1));
            }
            return report;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving disaster report: " + e.getMessage(), e);
        }
    }

    public DisasterReport findById(Long id) {
        String sql = "SELECT r.*, u.name as reporter_name, u.email as reporter_email, " +
                "d.name as disaster_name, d.code as disaster_code, " +
                "v.name as verifier_name, v.email as verifier_email " +
                "FROM disaster_reports r " +
                "JOIN users u ON r.reporter_id = u.id " +
                "JOIN disaster_types d ON r.disaster_type_id = d.id " +
                "LEFT JOIN users v ON r.verified_by_id = v.id " +
                "WHERE r.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReport(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding disaster report by id: " + e.getMessage(), e);
        }
    }

    public DisasterReport findByReportCode(String code) {
        String sql = "SELECT r.*, u.name as reporter_name, u.email as reporter_email, " +
                "d.name as disaster_name, d.code as disaster_code, " +
                "v.name as verifier_name, v.email as verifier_email " +
                "FROM disaster_reports r " +
                "JOIN users u ON r.reporter_id = u.id " +
                "JOIN disaster_types d ON r.disaster_type_id = d.id " +
                "LEFT JOIN users v ON r.verified_by_id = v.id " +
                "WHERE r.report_code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapReport(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding disaster report by code: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(Long id, ReportStatus status) {
        String sql = "UPDATE disaster_reports SET status = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(3, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating report status: " + e.getMessage(), e);
        }
    }

    public boolean verifyReport(Long id, Long officerId) {
        String sql = "UPDATE disaster_reports SET status = ?, verified_by_id = ?, verified_at = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ReportStatus.VERIFIED.name());
            ps.setLong(2, officerId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(5, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error verifying report: " + e.getMessage(), e);
        }
    }

    public boolean rejectReport(Long id, Long officerId, String reason) {
        String sql = "UPDATE disaster_reports SET status = ?, verified_by_id = ?, verified_at = ?, rejection_reason = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ReportStatus.REJECTED.name());
            ps.setLong(2, officerId);
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(4, reason);
            ps.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(6, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error rejecting report: " + e.getMessage(), e);
        }
    }

    public List<DisasterReport> findMyReports(Long reporterId, int page, int size) {
        String sql = "SELECT r.*, u.name as reporter_name, u.email as reporter_email, " +
                "d.name as disaster_name, d.code as disaster_code, " +
                "v.name as verifier_name, v.email as verifier_email " +
                "FROM disaster_reports r " +
                "JOIN users u ON r.reporter_id = u.id " +
                "JOIN disaster_types d ON r.disaster_type_id = d.id " +
                "LEFT JOIN users v ON r.verified_by_id = v.id " +
                "WHERE r.reporter_id = ? " +
                "ORDER BY r.created_at DESC LIMIT ? OFFSET ?";
        List<DisasterReport> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reporterId);
            ps.setInt(2, size);
            ps.setInt(3, page * size);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReport(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching user reports: " + e.getMessage(), e);
        }
    }

    public long countMyReports(Long reporterId) {
        String sql = "SELECT COUNT(*) FROM disaster_reports WHERE reporter_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reporterId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting user reports: " + e.getMessage(), e);
        }
    }

    public List<DisasterReport> findAll(String status, String severity, String query, int page, int size) {
        StringBuilder sql = new StringBuilder("SELECT r.*, u.name as reporter_name, u.email as reporter_email, ")
                .append("d.name as disaster_name, d.code as disaster_code, ")
                .append("v.name as verifier_name, v.email as verifier_email ")
                .append("FROM disaster_reports r ")
                .append("JOIN users u ON r.reporter_id = u.id ")
                .append("JOIN disaster_types d ON r.disaster_type_id = d.id ")
                .append("LEFT JOIN users v ON r.verified_by_id = v.id WHERE 1=1 ");

        List<Object> params = new ArrayList<>();
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND r.status = ? ");
            params.add(status.trim().toUpperCase());
        }
        if (severity != null && !severity.trim().isEmpty() && !"ALL".equalsIgnoreCase(severity)) {
            sql.append("AND r.severity = ? ");
            params.add(severity.trim().toUpperCase());
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(r.title) LIKE ? OR LOWER(r.description) LIKE ? OR LOWER(r.address) LIKE ? OR LOWER(r.report_code) LIKE ?) ");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }
        sql.append("ORDER BY r.created_at DESC LIMIT ? OFFSET ?");
        params.add(size);
        params.add(page * size);

        List<DisasterReport> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapReport(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reports: " + e.getMessage(), e);
        }
    }

    public long countAll(String status, String severity, String query) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM disaster_reports r WHERE 1=1 ");
        List<Object> params = new ArrayList<>();
        if (status != null && !status.trim().isEmpty() && !"ALL".equalsIgnoreCase(status)) {
            sql.append("AND r.status = ? ");
            params.add(status.trim().toUpperCase());
        }
        if (severity != null && !severity.trim().isEmpty() && !"ALL".equalsIgnoreCase(severity)) {
            sql.append("AND r.severity = ? ");
            params.add(severity.trim().toUpperCase());
        }
        if (query != null && !query.trim().isEmpty()) {
            sql.append("AND (LOWER(r.title) LIKE ? OR LOWER(r.description) LIKE ? OR LOWER(r.address) LIKE ? OR LOWER(r.report_code) LIKE ?) ");
            String q = "%" + query.trim().toLowerCase() + "%";
            params.add(q);
            params.add(q);
            params.add(q);
            params.add(q);
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reports: " + e.getMessage(), e);
        }
    }

    public List<DisasterReport> findActiveMapIncidents() {
        String sql = "SELECT r.*, u.name as reporter_name, u.email as reporter_email, " +
                "d.name as disaster_name, d.code as disaster_code, " +
                "v.name as verifier_name, v.email as verifier_email " +
                "FROM disaster_reports r " +
                "JOIN users u ON r.reporter_id = u.id " +
                "JOIN disaster_types d ON r.disaster_type_id = d.id " +
                "LEFT JOIN users v ON r.verified_by_id = v.id " +
                "WHERE r.status IN ('SUBMITTED', 'VERIFIED', 'ASSIGNED', 'IN_PROGRESS') " +
                "ORDER BY r.created_at DESC";
        List<DisasterReport> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapReport(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching map incidents: " + e.getMessage(), e);
        }
    }

    public long countByStatus(ReportStatus status) {
        String sql = "SELECT COUNT(*) FROM disaster_reports WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status.name());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting reports by status: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM disaster_reports";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting total reports: " + e.getMessage(), e);
        }
    }

    private DisasterReport mapReport(ResultSet rs) throws SQLException {
        DisasterReport r = new DisasterReport();
        r.setId(rs.getLong("id"));
        r.setReportCode(rs.getString("report_code"));
        r.setReporterId(rs.getLong("reporter_id"));
        r.setDisasterTypeId(rs.getLong("disaster_type_id"));
        r.setSeverity(SeverityLevel.valueOf(rs.getString("severity")));
        r.setStatus(ReportStatus.valueOf(rs.getString("status")));
        r.setTitle(rs.getString("title"));
        r.setDescription(rs.getString("description"));
        r.setAddress(rs.getString("address"));
        r.setLatitude(rs.getDouble("latitude"));
        r.setLongitude(rs.getDouble("longitude"));
        r.setAftermath(rs.getString("aftermath"));
        r.setContactPhone(rs.getString("contact_phone"));
        r.setRejectionReason(rs.getString("rejection_reason"));

        long verifierId = rs.getLong("verified_by_id");
        if (!rs.wasNull()) {
            r.setVerifiedById(verifierId);
            User v = new User();
            v.setId(verifierId);
            v.setName(rs.getString("verifier_name"));
            v.setEmail(rs.getString("verifier_email"));
            r.setVerifiedBy(v);
        }

        Timestamp va = rs.getTimestamp("verified_at");
        if (va != null) r.setVerifiedAt(va.toLocalDateTime());
        Timestamp ca = rs.getTimestamp("created_at");
        if (ca != null) r.setCreatedAt(ca.toLocalDateTime());
        Timestamp ua = rs.getTimestamp("updated_at");
        if (ua != null) r.setUpdatedAt(ua.toLocalDateTime());

        User reporter = new User();
        reporter.setId(r.getReporterId());
        reporter.setName(rs.getString("reporter_name"));
        reporter.setEmail(rs.getString("reporter_email"));
        r.setReporter(reporter);

        DisasterType dt = new DisasterType();
        dt.setId(r.getDisasterTypeId());
        dt.setName(rs.getString("disaster_name"));
        dt.setCode(rs.getString("disaster_code"));
        r.setDisasterType(dt);

        return r;
    }
}
