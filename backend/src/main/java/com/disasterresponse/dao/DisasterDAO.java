package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.SeverityLevel;
import com.disasterresponse.model.DisasterType;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DisasterDAO {

    public List<DisasterType> findAll() {
        String sql = "SELECT * FROM disaster_types WHERE is_active = true ORDER BY name ASC";
        List<DisasterType> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching disaster types: " + e.getMessage(), e);
        }
    }

    public List<DisasterType> findAllIncludingInactive() {
        String sql = "SELECT * FROM disaster_types ORDER BY id ASC";
        List<DisasterType> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all disaster types: " + e.getMessage(), e);
        }
    }

    public DisasterType findById(Long id) {
        String sql = "SELECT * FROM disaster_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding disaster type by id: " + e.getMessage(), e);
        }
    }

    public DisasterType findByCode(String code) {
        String sql = "SELECT * FROM disaster_types WHERE code = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding disaster type by code: " + e.getMessage(), e);
        }
    }

    public DisasterType save(DisasterType dt) {
        String sql = "INSERT INTO disaster_types (name, code, description, icon, default_severity, warning_signs, safety_procedures, do_list, dont_list, before_steps, during_steps, after_steps, is_active) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, dt.getName());
            ps.setString(2, dt.getCode());
            ps.setString(3, dt.getDescription());
            ps.setString(4, dt.getIcon());
            ps.setString(5, dt.getDefaultSeverity() != null ? dt.getDefaultSeverity().name() : SeverityLevel.MEDIUM.name());
            ps.setString(6, dt.getWarningSigns());
            ps.setString(7, dt.getSafetyProcedures());
            ps.setString(8, dt.getDoList());
            ps.setString(9, dt.getDontList());
            ps.setString(10, dt.getBeforeSteps());
            ps.setString(11, dt.getDuringSteps());
            ps.setString(12, dt.getAfterSteps());
            ps.setBoolean(13, dt.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    dt.setId(rs.getLong(1));
                }
            }
            return dt;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving disaster type: " + e.getMessage(), e);
        }
    }

    public boolean update(DisasterType dt) {
        String sql = "UPDATE disaster_types SET name = ?, code = ?, description = ?, icon = ?, default_severity = ?, warning_signs = ?, safety_procedures = ?, do_list = ?, dont_list = ?, before_steps = ?, during_steps = ?, after_steps = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, dt.getName());
            ps.setString(2, dt.getCode());
            ps.setString(3, dt.getDescription());
            ps.setString(4, dt.getIcon());
            ps.setString(5, dt.getDefaultSeverity() != null ? dt.getDefaultSeverity().name() : SeverityLevel.MEDIUM.name());
            ps.setString(6, dt.getWarningSigns());
            ps.setString(7, dt.getSafetyProcedures());
            ps.setString(8, dt.getDoList());
            ps.setString(9, dt.getDontList());
            ps.setString(10, dt.getBeforeSteps());
            ps.setString(11, dt.getDuringSteps());
            ps.setString(12, dt.getAfterSteps());
            ps.setBoolean(13, dt.isActive());
            ps.setLong(14, dt.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating disaster type: " + e.getMessage(), e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM disaster_types WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting disaster type: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM disaster_types";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting disaster types: " + e.getMessage(), e);
        }
    }

    private DisasterType mapRow(ResultSet rs) throws SQLException {
        DisasterType dt = new DisasterType();
        dt.setId(rs.getLong("id"));
        dt.setName(rs.getString("name"));
        dt.setCode(rs.getString("code"));
        dt.setDescription(rs.getString("description"));
        dt.setIcon(rs.getString("icon"));
        String sev = rs.getString("default_severity");
        if (sev != null) dt.setDefaultSeverity(SeverityLevel.valueOf(sev));
        dt.setWarningSigns(rs.getString("warning_signs"));
        dt.setSafetyProcedures(rs.getString("safety_procedures"));
        dt.setDoList(rs.getString("do_list"));
        dt.setDontList(rs.getString("dont_list"));
        dt.setBeforeSteps(rs.getString("before_steps"));
        dt.setDuringSteps(rs.getString("during_steps"));
        dt.setAfterSteps(rs.getString("after_steps"));
        dt.setActive(rs.getBoolean("is_active"));
        return dt;
    }
}
