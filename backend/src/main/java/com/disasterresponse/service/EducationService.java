package com.disasterresponse.service;

import com.disasterresponse.dao.*;
import com.disasterresponse.dto.*;
import com.disasterresponse.model.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EducationService {

    private final DisasterDAO disasterDAO;
    private final LearningModuleDAO moduleDAO;
    private final QuizDAO quizDAO;
    private final SimulationDAO simulationDAO;

    public EducationService() {
        this.disasterDAO = new DisasterDAO();
        this.moduleDAO = new LearningModuleDAO();
        this.quizDAO = new QuizDAO();
        this.simulationDAO = new SimulationDAO();
    }

    // -------------------------------------------------------------
    // Disaster Types
    // -------------------------------------------------------------
    public List<DisasterType> getAllDisasters() {
        return disasterDAO.findAll();
    }

    public DisasterType getDisasterById(Long id) {
        DisasterType dt = disasterDAO.findById(id);
        if (dt == null) {
            throw new IllegalArgumentException("Disaster type not found with ID: " + id);
        }
        return dt;
    }

    public DisasterType createDisaster(DisasterType dt) {
        if (disasterDAO.findByCode(dt.getCode()) != null) {
            throw new IllegalArgumentException("Disaster type with code '" + dt.getCode() + "' already exists.");
        }
        return disasterDAO.save(dt);
    }

    public DisasterType updateDisaster(Long id, DisasterType dt) {
        DisasterType existing = getDisasterById(id);
        dt.setId(existing.getId());
        disasterDAO.update(dt);
        return dt;
    }

    public void deleteDisaster(Long id) {
        disasterDAO.delete(id);
    }

    // -------------------------------------------------------------
    // Learning Modules
    // -------------------------------------------------------------
    public List<LearningModuleDto> getAllModules(Long userId) {
        List<LearningModule> modules = moduleDAO.findAll();
        List<LearningModuleDto> dtos = new ArrayList<>();
        for (LearningModule m : modules) {
            DisasterType dt = disasterDAO.findById(m.getDisasterTypeId());
            m.setDisasterType(dt);
            LearningModuleDto dto = mapToModuleDto(m);
            if (userId != null) {
                ModuleProgress prog = moduleDAO.getProgress(userId, m.getId());
                if (prog != null) {
                    dto.setUserProgress(prog.getCompletionPercentage());
                    dto.setUserCompleted(prog.isCompleted());
                    dto.setLastAccessedAt(prog.getLastAccessedAt());
                } else {
                    dto.setUserProgress(0);
                    dto.setUserCompleted(false);
                }
            }
            dtos.add(dto);
        }
        return dtos;
    }

    public LearningModuleDto getModuleById(Long id, Long userId) {
        LearningModule m = moduleDAO.findById(id);
        if (m == null) {
            throw new IllegalArgumentException("Learning module not found with ID: " + id);
        }
        DisasterType dt = disasterDAO.findById(m.getDisasterTypeId());
        m.setDisasterType(dt);
        LearningModuleDto dto = mapToModuleDto(m);
        if (userId != null) {
            ModuleProgress prog = moduleDAO.getProgress(userId, m.getId());
            if (prog != null) {
                dto.setUserProgress(prog.getCompletionPercentage());
                dto.setUserCompleted(prog.isCompleted());
                dto.setLastAccessedAt(prog.getLastAccessedAt());
            } else {
                dto.setUserProgress(0);
                dto.setUserCompleted(false);
            }
        }
        return dto;
    }

    public LearningModule createModule(LearningModule m) {
        return moduleDAO.save(m);
    }

    public LearningModule updateModule(Long id, LearningModule m) {
        LearningModule existing = moduleDAO.findById(id);
        if (existing == null) throw new IllegalArgumentException("Module not found: " + id);
        m.setId(existing.getId());
        moduleDAO.update(m);
        return m;
    }

    public void deleteModule(Long id) {
        moduleDAO.delete(id);
    }

    public ModuleProgressDto updateProgress(Long userId, Long moduleId, ModuleProgressDto dto) {
        int percentage = Math.min(100, Math.max(0, dto.getCompletionPercentage() != null ? dto.getCompletionPercentage() : 0));
        boolean completed = Boolean.TRUE.equals(dto.getCompleted()) || percentage >= 100;

        ModuleProgress prog = new ModuleProgress(userId, moduleId, completed, percentage);
        moduleDAO.saveOrUpdateProgress(prog);

        dto.setUserId(userId);
        dto.setModuleId(moduleId);
        dto.setCompleted(completed);
        dto.setCompletionPercentage(percentage);
        dto.setLastAccessedAt(prog.getLastAccessedAt());
        dto.setCompletedAt(prog.getCompletedAt());
        return dto;
    }

    // -------------------------------------------------------------
    // Quizzes
    // -------------------------------------------------------------
    public List<Quiz> getAllQuizzes() {
        return quizDAO.findAll();
    }

    public Quiz getQuizById(Long id) {
        Quiz q = quizDAO.findById(id);
        if (q == null) {
            throw new IllegalArgumentException("Quiz not found with ID: " + id);
        }
        return q;
    }

    public QuizResultDto submitQuiz(Long userId, Long quizId, QuizSubmitRequest request) {
        Quiz quiz = getQuizById(quizId);
        List<QuizQuestion> questions = quiz.getQuestions();

        int correctCount = 0;
        int totalQuestions = questions.size();
        Map<Long, Long> answers = request.getAnswers() != null ? request.getAnswers() : Map.of();

        for (QuizQuestion q : questions) {
            Long selectedOptId = answers.get(q.getId());
            if (selectedOptId != null) {
                for (QuizOption opt : q.getOptions()) {
                    if (opt.getId().equals(selectedOptId) && opt.isCorrect()) {
                        correctCount++;
                        break;
                    }
                }
            }
        }

        int scorePercentage = totalQuestions > 0 ? (int) Math.round(((double) correctCount / totalQuestions) * 100.0) : 0;
        boolean passed = scorePercentage >= quiz.getPassingScore();

        QuizResult qr = new QuizResult(userId, quizId, scorePercentage, totalQuestions, correctCount, passed);
        qr = quizDAO.saveResult(qr);

        QuizResultDto resultDto = new QuizResultDto();
        resultDto.setId(qr.getId());
        resultDto.setQuizId(quiz.getId());
        resultDto.setQuizTitle(quiz.getTitle());
        resultDto.setScore(scorePercentage);
        resultDto.setTotalQuestions(totalQuestions);
        resultDto.setCorrectAnswers(correctCount);
        resultDto.setPassed(passed);
        resultDto.setCompletedAt(qr.getCompletedAt());
        return resultDto;
    }

    public List<QuizResultDto> getMyQuizResults(Long userId) {
        List<QuizResult> list = quizDAO.getResultsByUser(userId);
        return list.stream().map(r -> {
            QuizResultDto dto = new QuizResultDto();
            dto.setId(r.getId());
            dto.setQuizId(r.getQuizId());
            dto.setQuizTitle(r.getQuiz() != null ? r.getQuiz().getTitle() : "Quiz #" + r.getQuizId());
            dto.setScore(r.getScore());
            dto.setTotalQuestions(r.getTotalQuestions());
            dto.setCorrectAnswers(r.getCorrectAnswers());
            dto.setPassed(r.isPassed());
            dto.setCompletedAt(r.getCompletedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    public Quiz createQuiz(Quiz quiz) {
        Quiz saved = quizDAO.save(quiz);
        if (quiz.getQuestions() != null) {
            for (QuizQuestion q : quiz.getQuestions()) {
                q.setQuizId(saved.getId());
                QuizQuestion savedQ = quizDAO.saveQuestion(q);
                if (q.getOptions() != null) {
                    for (QuizOption opt : q.getOptions()) {
                        opt.setQuestionId(savedQ.getId());
                        quizDAO.saveOption(opt);
                    }
                }
            }
        }
        return getQuizById(saved.getId());
    }

    public Quiz updateQuiz(Long id, Quiz quiz) {
        Quiz existing = getQuizById(id);
        quiz.setId(existing.getId());
        quizDAO.update(quiz);
        return getQuizById(id);
    }

    public void deleteQuiz(Long id) {
        quizDAO.delete(id);
    }

    // -------------------------------------------------------------
    // Simulations
    // -------------------------------------------------------------
    public List<Simulation> getAllSimulations() {
        return simulationDAO.findAll();
    }

    public Simulation createSimulation(Simulation sim) {
        Simulation saved = simulationDAO.save(sim);
        if (sim.getOptions() != null) {
            for (SimulationOption opt : sim.getOptions()) {
                opt.setSimulationId(saved.getId());
                simulationDAO.saveOption(opt);
            }
        }
        return getSimulationById(saved.getId());
    }

    public Simulation updateSimulation(Long id, Simulation sim) {
        Simulation existing = getSimulationById(id);
        sim.setId(existing.getId());
        simulationDAO.update(sim);
        return getSimulationById(id);
    }

    public void deleteSimulation(Long id) {
        simulationDAO.delete(id);
    }

    public Simulation getSimulationById(Long id) {
        Simulation s = simulationDAO.findById(id);
        if (s == null) {
            throw new IllegalArgumentException("Simulation not found with ID: " + id);
        }
        return s;
    }

    public SimulationResultDto submitSimulation(Long userId, Long simId, SimulationSubmitRequest request) {
        Simulation sim = getSimulationById(simId);
        Long optId = request.getSelectedOptionId();

        SimulationOption selected = null;
        for (SimulationOption opt : sim.getOptions()) {
            if (opt.getId().equals(optId)) {
                selected = opt;
                break;
            }
        }

        if (selected == null) {
            throw new IllegalArgumentException("Invalid simulation option selected.");
        }

        int scoreEarned = Math.max(0, selected.getScoreDelta());
        SimulationResult sr = new SimulationResult(userId, simId, optId, scoreEarned);
        sr = simulationDAO.saveResult(sr);

        SimulationResultDto dto = new SimulationResultDto();
        dto.setId(sr.getId());
        dto.setSimulationId(sim.getId());
        dto.setSimulationTitle(sim.getTitle());
        dto.setSelectedOptionId(selected.getId());
        dto.setActionText(selected.getActionText());
        dto.setConsequenceText(selected.getConsequenceText());
        dto.setAppropriate(selected.isAppropriate());
        dto.setScoreEarned(scoreEarned);
        dto.setSafetyFeedback(selected.getSafetyFeedback());
        dto.setCompletedAt(sr.getCompletedAt());
        return dto;
    }

    public List<SimulationResultDto> getMySimulationResults(Long userId) {
        List<SimulationResult> list = simulationDAO.getResultsByUser(userId);
        return list.stream().map(r -> {
            SimulationResultDto dto = new SimulationResultDto();
            dto.setId(r.getId());
            dto.setSimulationId(r.getSimulationId());
            dto.setSimulationTitle(r.getSimulation() != null ? r.getSimulation().getTitle() : "Simulation #" + r.getSimulationId());
            dto.setSelectedOptionId(r.getSelectedOptionId());
            if (r.getSelectedOption() != null) {
                dto.setActionText(r.getSelectedOption().getActionText());
                dto.setAppropriate(r.getSelectedOption().isAppropriate());
            }
            dto.setScoreEarned(r.getScoreEarned());
            dto.setCompletedAt(r.getCompletedAt());
            return dto;
        }).collect(Collectors.toList());
    }

    // -------------------------------------------------------------
    // Preparedness Assessment Score
    // -------------------------------------------------------------
    public PreparednessScoreDto calculatePreparednessScore(Long userId) {
        long totalModules = moduleDAO.countTotal();
        long completedModules = moduleDAO.countCompletedByUser(userId);

        List<QuizResult> userQuizResults = quizDAO.getResultsByUser(userId);
        double quizAverage = userQuizResults.isEmpty() ? 0.0 :
                userQuizResults.stream().mapToInt(QuizResult::getScore).average().orElse(0.0);

        long totalSimulations = simulationDAO.countTotal();
        long completedSimulations = simulationDAO.countCompletedByUser(userId);

        int moduleScore = totalModules > 0 ? (int) Math.round(((double) completedModules / totalModules) * 100.0) : 0;
        int quizScore = (int) Math.round(quizAverage);
        int simulationScore = totalSimulations > 0 ? (int) Math.round(((double) completedSimulations / totalSimulations) * 100.0) : 0;

        int overallScore = (int) Math.round((0.30 * moduleScore) + (0.40 * quizScore) + (0.30 * simulationScore));
        overallScore = Math.min(100, Math.max(0, overallScore));

        String level;
        if (overallScore >= 85) {
            level = "READINESS CHAMPION";
        } else if (overallScore >= 65) {
            level = "PREPARED";
        } else if (overallScore >= 40) {
            level = "INTERMEDIATE";
        } else {
            level = "BEGINNER";
        }

        PreparednessScoreDto dto = new PreparednessScoreDto();
        dto.setOverallScore(overallScore);
        dto.setModuleCompletionScore(moduleScore);
        dto.setQuizPerformanceScore(quizScore);
        dto.setSimulationPerformanceScore(simulationScore);
        dto.setModulesCompleted(completedModules);
        dto.setTotalModules(totalModules);
        dto.setAverageQuizScore(Math.round(quizAverage * 10.0) / 10.0);
        dto.setSimulationsCompleted(completedSimulations);
        dto.setTotalSimulations(totalSimulations);
        dto.setPreparednessLevel(level);

        return dto;
    }

    private LearningModuleDto mapToModuleDto(LearningModule m) {
        LearningModuleDto dto = new LearningModuleDto();
        dto.setId(m.getId());
        dto.setTitle(m.getTitle());
        dto.setDisasterTypeId(m.getDisasterTypeId());
        if (m.getDisasterType() != null) {
            dto.setDisasterTypeName(m.getDisasterType().getName());
            dto.setDisasterCode(m.getDisasterType().getCode());
            dto.setDefaultSeverity(m.getDisasterType().getDefaultSeverity());
        }
        dto.setSummary(m.getSummary());
        dto.setContent(m.getContent());
        dto.setEstimatedMinutes(m.getEstimatedMinutes());
        dto.setMediaUrl(m.getMediaUrl());
        dto.setOrderIndex(m.getOrderIndex());
        dto.setActive(m.isActive());
        dto.setCreatedAt(m.getCreatedAt());
        return dto;
    }
}
