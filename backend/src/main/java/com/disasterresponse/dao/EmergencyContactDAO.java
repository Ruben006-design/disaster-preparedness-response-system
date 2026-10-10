package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.model.EmergencyContact;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmergencyContactDAO {

    public List<EmergencyContact> findAll() {
        String sql = "SELECT * FROM emergency_contacts WHERE is_active = true ORDER BY priority_order ASC, service_name ASC";
        List<EmergencyContact> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapContact(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching emergency contacts: " + e.getMessage(), e);
        }
    }

    public List<EmergencyContact> findAllIncludingInactive() {
        String sql = "SELECT * FROM emergency_contacts ORDER BY priority_order ASC, id ASC";
        List<EmergencyContact> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapContact(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all emergency contacts: " + e.getMessage(), e);
        }
    }

    public EmergencyContact findById(Long id) {
        String sql = "SELECT * FROM emergency_contacts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapContact(rs);
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding contact by id: " + e.getMessage(), e);
        }
    }

    public EmergencyContact save(EmergencyContact c) {
        String sql = "INSERT INTO emergency_contacts (service_name, organization, phone_number, alternate_phone, location, priority_order, is_active) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, c.getServiceName());
            ps.setString(2, c.getOrganization());
            ps.setString(3, c.getPhoneNumber());
            ps.setString(4, c.getAlternatePhone());
            ps.setString(5, c.getLocation());
            ps.setInt(6, c.getPriorityOrder() != null ? c.getPriorityOrder() : 0);
            ps.setBoolean(7, c.isActive());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getLong(1));
            }
            return c;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving contact: " + e.getMessage(), e);
        }
    }

    public boolean update(EmergencyContact c) {
        String sql = "UPDATE emergency_contacts SET service_name = ?, organization = ?, phone_number = ?, alternate_phone = ?, location = ?, priority_order = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getServiceName());
            ps.setString(2, c.getOrganization());
            ps.setString(3, c.getPhoneNumber());
            ps.setString(4, c.getAlternatePhone());
            ps.setString(5, c.getLocation());
            ps.setInt(6, c.getPriorityOrder() != null ? c.getPriorityOrder() : 0);
            ps.setBoolean(7, c.isActive());
            ps.setLong(8, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating contact: " + e.getMessage(), e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM emergency_contacts WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting contact: " + e.getMessage(), e);
        }
    }

    private EmergencyContact mapContact(ResultSet rs) throws SQLException {
        EmergencyContact c = new EmergencyContact();
        c.setId(rs.getLong("id"));
        c.setServiceName(rs.getString("service_name"));
        c.setOrganization(rs.getString("organization"));
        c.setPhoneNumber(rs.getString("phone_number"));
        c.setAlternatePhone(rs.getString("alternate_phone"));
        c.setLocation(rs.getString("location"));
        c.setPriorityOrder(rs.getInt("priority_order"));
        c.setActive(rs.getBoolean("is_active"));
        return c;
    }
}
