package com.disasterresponse.dao;

import com.disasterresponse.database.DatabaseConnection;
import com.disasterresponse.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public List<Quiz> findAll() {
        String sql = "SELECT q.*, d.name as disaster_name FROM quizzes q " +
                "JOIN disaster_types d ON q.disaster_type_id = d.id " +
                "WHERE q.is_active = true ORDER BY q.id ASC";
        List<Quiz> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapQuiz(rs));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching quizzes: " + e.getMessage(), e);
        }
    }

    public Quiz findById(Long id) {
        String sql = "SELECT q.*, d.name as disaster_name FROM quizzes q " +
                "JOIN disaster_types d ON q.disaster_type_id = d.id " +
                "WHERE q.id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Quiz q = mapQuiz(rs);
                    q.setQuestions(getQuestionsByQuizId(q.getId(), conn));
                    return q;
                }
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException("Error finding quiz by id: " + e.getMessage(), e);
        }
    }

    private List<QuizQuestion> getQuestionsByQuizId(Long quizId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM quiz_questions WHERE quiz_id = ? ORDER BY order_index ASC, id ASC";
        List<QuizQuestion> questions = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, quizId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizQuestion qq = new QuizQuestion();
                    qq.setId(rs.getLong("id"));
                    qq.setQuizId(rs.getLong("quiz_id"));
                    qq.setQuestionText(rs.getString("question_text"));
                    qq.setExplanation(rs.getString("explanation"));
                    qq.setPoints(rs.getInt("points"));
                    qq.setOrderIndex(rs.getInt("order_index"));
                    qq.setOptions(getOptionsByQuestionId(qq.getId(), conn));
                    questions.add(qq);
                }
            }
        }
        return questions;
    }

    private List<QuizOption> getOptionsByQuestionId(Long questionId, Connection conn) throws SQLException {
        String sql = "SELECT * FROM quiz_options WHERE question_id = ? ORDER BY id ASC";
        List<QuizOption> options = new ArrayList<>();
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, questionId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizOption qo = new QuizOption();
                    qo.setId(rs.getLong("id"));
                    qo.setQuestionId(rs.getLong("question_id"));
                    qo.setOptionText(rs.getString("option_text"));
                    qo.setCorrect(rs.getBoolean("is_correct"));
                    options.add(qo);
                }
            }
        }
        return options;
    }

    public Quiz save(Quiz quiz) {
        String sql = "INSERT INTO quizzes (title, disaster_type_id, description, passing_score, time_limit_minutes, is_active, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, quiz.getTitle());
            ps.setLong(2, quiz.getDisasterTypeId());
            ps.setString(3, quiz.getDescription());
            ps.setInt(4, quiz.getPassingScore());
            ps.setInt(5, quiz.getTimeLimitMinutes());
            ps.setBoolean(6, quiz.isActive());
            ps.setTimestamp(7, Timestamp.valueOf(quiz.getCreatedAt() != null ? quiz.getCreatedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) quiz.setId(rs.getLong(1));
            }
            return quiz;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz: " + e.getMessage(), e);
        }
    }

    public QuizQuestion saveQuestion(QuizQuestion q) {
        String sql = "INSERT INTO quiz_questions (quiz_id, question_text, explanation, points, order_index) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, q.getQuizId());
            ps.setString(2, q.getQuestionText());
            ps.setString(3, q.getExplanation());
            ps.setInt(4, q.getPoints());
            ps.setInt(5, q.getOrderIndex());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) q.setId(rs.getLong(1));
            }
            return q;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz question: " + e.getMessage(), e);
        }
    }

    public QuizOption saveOption(QuizOption opt) {
        String sql = "INSERT INTO quiz_options (question_id, option_text, is_correct) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, opt.getQuestionId());
            ps.setString(2, opt.getOptionText());
            ps.setBoolean(3, opt.isCorrect());

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) opt.setId(rs.getLong(1));
            }
            return opt;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz option: " + e.getMessage(), e);
        }
    }

    public QuizResult saveResult(QuizResult qr) {
        String sql = "INSERT INTO quiz_results (user_id, quiz_id, score, total_questions, correct_answers, is_passed, completed_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setLong(1, qr.getUserId());
            ps.setLong(2, qr.getQuizId());
            ps.setInt(3, qr.getScore());
            ps.setInt(4, qr.getTotalQuestions());
            ps.setInt(5, qr.getCorrectAnswers());
            ps.setBoolean(6, qr.isPassed());
            ps.setTimestamp(7, Timestamp.valueOf(qr.getCompletedAt() != null ? qr.getCompletedAt() : java.time.LocalDateTime.now()));

            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) qr.setId(rs.getLong(1));
            }
            return qr;
        } catch (SQLException e) {
            throw new RuntimeException("Error saving quiz result: " + e.getMessage(), e);
        }
    }

    public List<QuizResult> getResultsByUser(Long userId) {
        String sql = "SELECT qr.*, q.title as quiz_title FROM quiz_results qr " +
                "JOIN quizzes q ON qr.quiz_id = q.id " +
                "WHERE qr.user_id = ? ORDER BY qr.completed_at DESC";
        List<QuizResult> list = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    QuizResult r = new QuizResult();
                    r.setId(rs.getLong("id"));
                    r.setUserId(rs.getLong("user_id"));
                    r.setQuizId(rs.getLong("quiz_id"));
                    r.setScore(rs.getInt("score"));
                    r.setTotalQuestions(rs.getInt("total_questions"));
                    r.setCorrectAnswers(rs.getInt("correct_answers"));
                    r.setPassed(rs.getBoolean("is_passed"));
                    Timestamp c = rs.getTimestamp("completed_at");
                    if (c != null) r.setCompletedAt(c.toLocalDateTime());
                    list.add(r);
                }
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException("Error getting quiz results: " + e.getMessage(), e);
        }
    }

    public long countPassedByUser(Long userId) {
        String sql = "SELECT COUNT(DISTINCT quiz_id) FROM quiz_results WHERE user_id = ? AND is_passed = true";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting passed quizzes: " + e.getMessage(), e);
        }
    }

    public void update(Quiz q) {
        String sql = "UPDATE quizzes SET title = ?, disaster_type_id = ?, description = ?, passing_score = ?, time_limit_minutes = ?, is_active = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, q.getTitle());
            ps.setLong(2, q.getDisasterTypeId());
            ps.setString(3, q.getDescription());
            ps.setInt(4, q.getPassingScore());
            ps.setInt(5, q.getTimeLimitMinutes());
            ps.setBoolean(6, q.isActive());
            ps.setLong(7, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating quiz: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        String sql = "DELETE FROM quizzes WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting quiz: " + e.getMessage(), e);
        }
    }

    public long countTotal() {
        String sql = "SELECT COUNT(*) FROM quizzes WHERE is_active = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getLong(1);
            return 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error counting quizzes: " + e.getMessage(), e);
        }
    }

    private Quiz mapQuiz(ResultSet rs) throws SQLException {
        Quiz q = new Quiz();
        q.setId(rs.getLong("id"));
        q.setTitle(rs.getString("title"));
        q.setDisasterTypeId(rs.getLong("disaster_type_id"));
        q.setDescription(rs.getString("description"));
        q.setPassingScore(rs.getInt("passing_score"));
        q.setTimeLimitMinutes(rs.getInt("time_limit_minutes"));
        q.setActive(rs.getBoolean("is_active"));
        Timestamp cr = rs.getTimestamp("created_at");
        if (cr != null) q.setCreatedAt(cr.toLocalDateTime());
        return q;
    }
}
