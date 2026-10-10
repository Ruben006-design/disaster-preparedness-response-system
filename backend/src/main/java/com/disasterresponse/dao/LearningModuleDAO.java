package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.model.LearningModule;
import com.disasterresponse.model.ModuleProgress;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LearningModuleDAO {

    public List<LearningModule> findAll() {
        String sql = "SELECT m.*, d.name as disaster_name, d.code as disaster_code, d.icon as disaster_icon " +
                "FROM learning_modules m " +
                "JOIN disaster_types d ON m.disaster_type_id = d.id " +
                "WHERE m.is_active = true " +
                "ORDER BY m.order_index ASC, m.id ASC";
        List<LearningModule> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching learning modules: " + e.getMessage(), e);
        }
    }

    public List<LearningModule> findAllIncludingInactive() {
        String sql = "SELECT m.*, d.name as disaster_name, d.code as disaster_code, d.icon as disaster_icon " +
                "FROM learning_modules m " +
                "JOIN disaster_types d ON m.disaster_type_id = d.id " +
                "ORDER BY m.id ASC";
        List<LearningModule> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all learning modules: " + e.getMessage(), e);
        }
    }

    public LearningModule findById(Long id) {
        String sql = "SELECT m.*, d.name as disaster_name, d.code as disaster_code, d.icon as disaster_icon " +
                "FROM learning_modules m " +
                "JOIN disaster_types d ON m.disaster_type_id = d.id " +
                "WHERE m.id = ?";
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
            throw new RuntimeException("Error finding learning module by id: " + e.getMessage(), e);
        }
    }

    public LearningModule save(LearningModule m) {
        String sql = "INSERT INTO learning_modules (title, disaster_type_id, summary, content, estimated_minutes, media_url, order_index, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, m.getTitle());
            ps.setLong(2, m.getDisasterTypeId());
            ps.setString(3, m.getSummary());
            ps.setString(4, m.getContent());
            ps.setInt(5, m.getEstimatedMinutes() != null ? m.getEstimatedMinutes() : 15);
            ps.setString(6, m.getMediaUrl());
            ps.setInt(7, m.getOrderIndex() != null ? m.getOrderIndex() : 0);
            ps.setBoolean(8, m.isActive());
            ps.setTimestamp(9, Timestamp.valueOf(m.getCreatedAt() != null ? m.getCreatedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getLong(1));
                }
            }
            return m;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving learning module: " + e.getMessage(), e);
        }
    }

    public boolean update(LearningModule m) {
        String sql = "UPDATE learning_modules SET title = ?, disaster_type_id = ?, summary = ?, content = ?, estimated_minutes = ?, media_url = ?, order_index = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, m.getTitle());
            ps.setLong(2, m.getDisasterTypeId());
            ps.setString(3, m.getSummary());
            ps.setString(4, m.getContent());
            ps.setInt(5, m.getEstimatedMinutes() != null ? m.getEstimatedMinutes() : 15);
            ps.setString(6, m.getMediaUrl());
            ps.setInt(7, m.getOrderIndex() != null ? m.getOrderIndex() : 0);
            ps.setBoolean(8, m.isActive());
            ps.setLong(9, m.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error updating learning module: " + e.getMessage(), e);
        }
    }

    public boolean delete(Long id) {
        String sql = "DELETE FROM learning_modules WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting learning module: " + e.getMessage(), e);
        }
    }

    // Module Progress tracking
    public ModuleProgress getProgress(Long userId, Long moduleId) {
        String sql = "SELECT * FROM module_progress WHERE user_id = ? AND module_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            ps.setLong(2, moduleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ModuleProgress mp = new ModuleProgress();
                    mp.setId(rs.getLong("id"));
                    mp.setUserId(rs.getLong("user_id"));
                    mp.setModuleId(rs.getLong("module_id"));
                    mp.setCompleted(rs.getBoolean("is_completed"));
                    mp.setCompletionPercentage(rs.getInt("completion_percentage"));
                    Timestamp la = rs.getTimestamp("last_accessed_at");
                    if (la != null) mp.setLastAccessedAt(la.toLocalDateTime());
                    Timestamp co = rs.getTimestamp("completed_at");
                    if (co != null) mp.setCompletedAt(co.toLocalDateTime());
                    return mp;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting module progress: " + e.getMessage(), e);
        }
    }

    public void saveOrUpdateProgress(ModuleProgress progress) {
        ModuleProgress existing = getProgress(progress.getUserId(), progress.getModuleId());
        if (existing == null) {
            String sql = "INSERT INTO module_progress (user_id, module_id, is_completed, completion_percentage, last_accessed_at, completed_at) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, progress.getUserId());
                ps.setLong(2, progress.getModuleId());
                ps.setBoolean(3, progress.isCompleted());
                ps.setInt(4, progress.getCompletionPercentage());
                ps.setTimestamp(5, Timestamp.valueOf(progress.getLastAccessedAt() != null ? progress.getLastAccessedAt() : java.time.LocalDateTime.now()));
                ps.setTimestamp(6, progress.getCompletedAt() != null ? Timestamp.valueOf(progress.getCompletedAt()) : null);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error saving module progress: " + e.getMessage(), e);
            }
        } else {
            String sql = "UPDATE module_progress SET is_completed = ?, completion_percentage = ?, last_accessed_at = ?, completed_at = ? WHERE id = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setBoolean(1, progress.isCompleted());
                ps.setInt(2, progress.getCompletionPercentage());
                ps.setTimestamp(3, Timestamp.valueOf(progress.getLastAccessedAt() != null ? progress.getLastAccessedAt() : java.time.LocalDateTime.now()));
                ps.setTimestamp(4, progress.getCompletedAt() != null ? Timestamp.valueOf(progress.getCompletedAt()) : null);
                ps.setLong(5, existing.getId());
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Error updating module progress: " + e.getMessage(), e);
            }
        }
    }

    public long countCompletedByUser(Long userId) {
        String sql = "SELECT COUNT(*) FROM module_progress WHERE user_id = ? AND is_completed = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting completed modules: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM learning_modules WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting learning modules: " + e.getMessage(), e);
        }
    }

    private LearningModule mapRow(ResultSet rs) throws SQLException {
        LearningModule m = new LearningModule();
        m.setId(rs.getLong("id"));
        m.setTitle(rs.getString("title"));
        m.setDisasterTypeId(rs.getLong("disaster_type_id"));
        m.setSummary(rs.getString("summary"));
        m.setContent(rs.getString("content"));
        m.setEstimatedMinutes(rs.getInt("estimated_minutes"));
        m.setMediaUrl(rs.getString("media_url"));
        m.setOrderIndex(rs.getInt("order_index"));
        m.setActive(rs.getBoolean("is_active"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) m.setCreatedAt(cr.toLocalDateTime());
        return m;
    }
}
