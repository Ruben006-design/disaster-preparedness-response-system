package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.model.ResponseTeam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResponseTeamDAO {

    public List<ResponseTeam> findAll() {
        String sql = "SELECT * FROM response_teams WHERE is_active = true ORDER BY team_name ASC";
        List<ResponseTeam> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapTeam(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching response teams: " + e.getMessage(), e);
        }
    }

    public List<ResponseTeam> findByDepartmentId(Long departmentId) {
        String sql = "SELECT * FROM response_teams WHERE department_id = ? AND is_active = true ORDER BY team_name ASC";
        List<ResponseTeam> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, departmentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapTeam(rs));
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching teams for department: " + e.getMessage(), e);
        }
    }

    public ResponseTeam findById(Long id) {
        String sql = "SELECT * FROM response_teams WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapTeam(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding response team by id: " + e.getMessage(), e);
        }
    }

    public ResponseTeam save(ResponseTeam team) {
        String sql = "INSERT INTO response_teams (department_id, team_name, team_lead, contact_phone, status, is_active) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, team.getDepartmentId());
            ps.setString(2, team.getTeamName());
            ps.setString(3, team.getTeamLead());
            ps.setString(4, team.getContactPhone());
            ps.setString(5, team.getStatus() != null ? team.getStatus() : "AVAILABLE");
            ps.setBoolean(6, team.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) team.setId(rs.getLong(1));
            }
            return team;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving response team: " + e.getMessage(), e);
        }
    }

    public boolean updateStatus(Long id, String status) {
        String sql = "UPDATE response_teams SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setLong(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating response team status: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM response_teams";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting response teams: " + e.getMessage(), e);
        }
    }

    private ResponseTeam mapTeam(ResultSet rs) throws SQLException {
        ResponseTeam t = new ResponseTeam();
        t.setId(rs.getLong("id"));
        t.setDepartmentId(rs.getLong("department_id"));
        t.setTeamName(rs.getString("team_name"));
        t.setTeamLead(rs.getString("team_lead"));
        t.setContactPhone(rs.getString("contact_phone"));
        t.setStatus(rs.getString("status"));
        t.setActive(rs.getBoolean("is_active"));
        return t;
    }
}
