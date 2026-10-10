package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.enums.SeverityLevel;
import com.disasterresponse.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SimulationDAO {

    public List<Simulation> findAll() {
        String sql = "SELECT s.*, d.name as disaster_name FROM simulations s " +
                "JOIN disaster_types d ON s.disaster_type_id = d.id " +
                "WHERE s.is_active = true ORDER BY s.id ASC";
        List<Simulation> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapSimulation(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching simulations: " + e.getMessage(), e);
        }
    }

    public Simulation findById(Long id) {
        String sql = "SELECT s.*, d.name as disaster_name FROM simulations s " +
                "JOIN disaster_types d ON s.disaster_type_id = d.id " +
                "WHERE s.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Simulation s = mapSimulation(rs);
                    s.setOptions(getOptionsBySimulationId(s.getId(), conn));
                    return s;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding simulation by id: " + e.getMessage(), e);
        }
    }

    private List<SimulationOption> getOptionsBySimulationId(Long simulationId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM simulation_options WHERE simulation_id = ? ORDER BY id ASC";
        List<SimulationOption> options = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, simulationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SimulationOption opt = new SimulationOption();
                    opt.setId(rs.getLong("id"));
                    opt.setSimulationId(rs.getLong("simulation_id"));
                    opt.setActionText(rs.getString("action_text"));
                    opt.setConsequenceText(rs.getString("consequence_text"));
                    opt.setScoreDelta(rs.getInt("score_delta"));
                    opt.setAppropriate(rs.getBoolean("is_appropriate"));
                    opt.setSafetyFeedback(rs.getString("safety_feedback"));
                    options.add(opt);
                }
            }
        }
        return options;
    }

    public Simulation save(Simulation sim) {
        String sql = "INSERT INTO simulations (title, disaster_type_id, scenario_text, location_context, initial_risk_level, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, sim.getTitle());
            ps.setLong(2, sim.getDisasterTypeId());
            ps.setString(3, sim.getScenarioText());
            ps.setString(4, sim.getLocationContext());
            ps.setString(5, sim.getInitialRiskLevel() != null ? sim.getInitialRiskLevel().name() : SeverityLevel.HIGH.name());
            ps.setBoolean(6, sim.isActive());
            ps.setTimestamp(7, Timestamp.valueOf(sim.getCreatedAt() != null ? sim.getCreatedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) sim.setId(rs.getLong(1));
            }
            return sim;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving simulation: " + e.getMessage(), e);
        }
    }

    public SimulationOption saveOption(SimulationOption opt) {
        String sql = "INSERT INTO simulation_options (simulation_id, action_text, consequence_text, score_delta, is_appropriate, safety_feedback) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, opt.getSimulationId());
            ps.setString(2, opt.getActionText());
            ps.setString(3, opt.getConsequenceText());
            ps.setInt(4, opt.getScoreDelta());
            ps.setBoolean(5, opt.isAppropriate());
            ps.setString(6, opt.getSafetyFeedback());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) opt.setId(rs.getLong(1));
            }
            return opt;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving simulation option: " + e.getMessage(), e);
        }
    }

    public SimulationResult saveResult(SimulationResult sr) {
        String sql = "INSERT INTO simulation_results (user_id, simulation_id, selected_option_id, score_earned, completed_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, sr.getUserId());
            ps.setLong(2, sr.getSimulationId());
            ps.setLong(3, sr.getSelectedOptionId());
            ps.setInt(4, sr.getScoreEarned());
            ps.setTimestamp(5, Timestamp.valueOf(sr.getCompletedAt() != null ? sr.getCompletedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) sr.setId(rs.getLong(1));
            }
            return sr;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving simulation result: " + e.getMessage(), e);
        }
    }

    public List<SimulationResult> getResultsByUser(Long userId) {
        String sql = "SELECT sr.*, s.title as sim_title, so.action_text, so.is_appropriate FROM simulation_results sr " +
                "JOIN simulations s ON sr.simulation_id = s.id " +
                "JOIN simulation_options so ON sr.selected_option_id = so.id " +
                "WHERE sr.user_id = ? ORDER BY sr.completed_at DESC";
        List<SimulationResult> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SimulationResult r = new SimulationResult();
                    r.setId(rs.getLong("id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setSimulationId(rs.getLong("simulation_id"));
                    r.setSelectedOptionId(rs.getLong("selected_option_id"));
                    r.setScoreEarned(rs.getInt("score_earned"));
                    Timestamp c = rs.getTimestamp("completed_at");
                    if (c != null) r.setCompletedAt(c.toLocalDateTime());

                    SimulationOption opt = new SimulationOption();
                    opt.setId(r.getSelectedOptionId());
                    opt.setActionText(rs.getString("action_text"));
                    opt.setAppropriate(rs.getBoolean("is_appropriate"));
                    r.setSelectedOption(opt);

                    list.add(r);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting simulation results: " + e.getMessage(), e);
        }
    }

    public long countCompletedByUser(Long userId) {
        String sql = "SELECT COUNT(DISTINCT simulation_id) FROM simulation_results WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting completed simulations: " + e.getMessage(), e);
        }
    }

    public void update(Simulation sim) {
        String sql = "UPDATE simulations SET title = ?, disaster_type_id = ?, scenario_text = ?, location_context = ?, initial_risk_level = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sim.getTitle());
            ps.setLong(2, sim.getDisasterTypeId());
            ps.setString(3, sim.getScenarioText());
            ps.setString(4, sim.getLocationContext());
            ps.setString(5, sim.getInitialRiskLevel() != null ? sim.getInitialRiskLevel().name() : SeverityLevel.HIGH.name());
            ps.setBoolean(6, sim.isActive());
            ps.setLong(7, sim.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating simulation: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM simulations WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting simulation: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM simulations WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting simulations: " + e.getMessage(), e);
        }
    }

    private Simulation mapSimulation(ResultSet rs) throws SQLException {
        Simulation s = new Simulation();
        s.setId(rs.getLong("id"));
        s.setTitle(rs.getString("title"));
        s.setDisasterTypeId(rs.getLong("disaster_type_id"));
        s.setScenarioText(rs.getString("scenario_text"));
        s.setLocationContext(rs.getString("location_context"));
        String risk = rs.getString("initial_risk_level");
        if (risk != null) s.setInitialRiskLevel(SeverityLevel.valueOf(risk));
        s.setActive(rs.getBoolean("is_active"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) s.setCreatedAt(cr.toLocalDateTime());
        return s;
    }
}
