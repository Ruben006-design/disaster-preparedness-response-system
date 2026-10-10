package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.SeverityLevel;
import com.disasterresponse.model.*;

import java.sql.*;

public class ResponseAssignmentDAO {

    public ResponseAssignment save(ResponseAssignment ra) {
        String sql = "INSERT INTO response_assignments (report_id, department_id, response_team_id, assigned_by_id, priority, instructions, assigned_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, ra.getReportId());
            ps.setLong(2, ra.getDepartmentId());
            if (ra.getResponseTeamId() != null) {
                ps.setLong(3, ra.getResponseTeamId());
            } else {
                ps.setNull(3, Types.BIGINT);
            }
            ps.setLong(4, ra.getAssignedById());
            ps.setString(5, ra.getPriority() != null ? ra.getPriority().name() : SeverityLevel.HIGH.name());
            ps.setString(6, ra.getInstructions());
            ps.setTimestamp(7, Timestamp.valueOf(ra.getAssignedAt() != null ? ra.getAssignedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) ra.setId(rs.getLong(1));
            }
            return ra;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving response assignment: " + e.getMessage(), e);
        }
    }

    public ResponseAssignment findByReportId(Long reportId) {
        String sql = "SELECT a.*, d.name as dept_name, d.code as dept_code, " +
                "t.team_name, t.team_lead, t.contact_phone as team_phone, " +
                "u.name as assigner_name " +
                "FROM response_assignments a " +
                "JOIN government_departments d ON a.department_id = d.id " +
                "LEFT JOIN response_teams t ON a.response_team_id = t.id " +
                "JOIN users u ON a.assigned_by_id = u.id " +
                "WHERE a.report_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, reportId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ResponseAssignment ra = new ResponseAssignment();
                    ra.setId(rs.getLong("id"));
                    ra.setReportId(rs.getLong("report_id"));
                    ra.setDepartmentId(rs.getLong("department_id"));
                    long teamId = rs.getLong("response_team_id");
                    if (!rs.wasNull()) {
                        ra.setResponseTeamId(teamId);
                        ResponseTeam rt = new ResponseTeam();
                        rt.setId(teamId);
                        rt.setTeamName(rs.getString("team_name"));
                        rt.setTeamLead(rs.getString("team_lead"));
                        rt.setContactPhone(rs.getString("team_phone"));
                        ra.setResponseTeam(rt);
                    }
                    ra.setAssignedById(rs.getLong("assigned_by_id"));
                    ra.setPriority(SeverityLevel.valueOf(rs.getString("priority")));
                    ra.setInstructions(rs.getString("instructions"));
                    Timestamp at = rs.getTimestamp("assigned_at");
                    if (at != null) ra.setAssignedAt(at.toLocalDateTime());

                    GovernmentDepartment gd = new GovernmentDepartment();
                    gd.setId(ra.getDepartmentId());
                    gd.setName(rs.getString("dept_name"));
                    gd.setCode(rs.getString("dept_code"));
                    ra.setDepartment(gd);

                    return ra;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding assignment for report: " + e.getMessage(), e);
        }
    }
}
