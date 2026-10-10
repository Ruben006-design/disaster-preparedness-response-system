package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.DepartmentType;
import com.disasterresponse.model.GovernmentDepartment;
import com.disasterresponse.model.ResponseTeam;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {

    public List<GovernmentDepartment> findAll() {
        String sql = "SELECT * FROM government_departments WHERE is_active = true ORDER BY name ASC";
        List<GovernmentDepartment> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapDept(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching departments: " + e.getMessage(), e);
        }

        // populate teams for each department
        for (GovernmentDepartment d : list) {
            d.setResponseTeams(findTeamsForDepartment(d.getId()));
        }

        return list;
    }

    public GovernmentDepartment findById(Long id) {
        String sql = "SELECT * FROM government_departments WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    GovernmentDepartment d = mapDept(rs);
                    d.setResponseTeams(findTeamsForDepartment(d.getId()));
                    return d;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding department by id: " + e.getMessage(), e);
        }
    }

    private List<ResponseTeam> findTeamsForDepartment(Long deptId) {
        String sql = "SELECT * FROM response_teams WHERE department_id = ? AND is_active = true ORDER BY team_name ASC";
        List<ResponseTeam> teams = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, deptId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ResponseTeam t = new ResponseTeam();
                    t.setId(rs.getLong("id"));
                    t.setDepartmentId(rs.getLong("department_id"));
                    t.setTeamName(rs.getString("team_name"));
                    t.setTeamLead(rs.getString("team_lead"));
                    t.setContactPhone(rs.getString("contact_phone"));
                    t.setStatus(rs.getString("status"));
                    t.setActive(rs.getBoolean("is_active"));
                    teams.add(t);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching teams for department: " + e.getMessage(), e);
        }
        return teams;
    }

    public GovernmentDepartment save(GovernmentDepartment dept) {
        String sql = "INSERT INTO government_departments (name, code, department_type, description, contact_email, contact_phone, head_name, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDepartmentType().name());
            ps.setString(4, dept.getDescription());
            ps.setString(5, dept.getContactEmail());
            ps.setString(6, dept.getContactPhone());
            ps.setString(7, dept.getHeadName());
            ps.setBoolean(8, dept.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) dept.setId(rs.getLong(1));
            }
            return dept;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving department: " + e.getMessage(), e);
        }
    }

    public boolean update(GovernmentDepartment dept) {
        String sql = "UPDATE government_departments SET name = ?, code = ?, department_type = ?, description = ?, contact_email = ?, contact_phone = ?, head_name = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dept.getName());
            ps.setString(2, dept.getCode());
            ps.setString(3, dept.getDepartmentType().name());
            ps.setString(4, dept.getDescription());
            ps.setString(5, dept.getContactEmail());
            ps.setString(6, dept.getContactPhone());
            ps.setString(7, dept.getHeadName());
            ps.setBoolean(8, dept.isActive());
            ps.setLong(9, dept.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating department: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM government_departments";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting departments: " + e.getMessage(), e);
        }
    }

    private GovernmentDepartment mapDept(ResultSet rs) throws SQLException {
        GovernmentDepartment d = new GovernmentDepartment();
        d.setId(rs.getLong("id"));
        d.setName(rs.getString("name"));
        d.setCode(rs.getString("code"));
        d.setDepartmentType(DepartmentType.valueOf(rs.getString("department_type")));
        d.setDescription(rs.getString("description"));
        d.setContactEmail(rs.getString("contact_email"));
        d.setContactPhone(rs.getString("contact_phone"));
        d.setHeadName(rs.getString("head_name"));
        d.setActive(rs.getBoolean("is_active"));
        return d;
    }
}
