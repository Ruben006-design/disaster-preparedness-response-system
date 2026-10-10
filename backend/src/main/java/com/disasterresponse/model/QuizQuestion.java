package com.disasterresponse.model;

import java.util.ArrayList;
import java.util.List;

public class QuizQuestion {
    private Long id;
    private Long quizId;
    private String questionText;
    private String explanation;
    private Integer points = 10;
    private Integer orderIndex = 0;
    private List<QuizOption> options = new ArrayList<>();

    public QuizQuestion() {
    }

    public QuizQuestion(Long quizId, String questionText, String explanation, Integer points, Integer orderIndex) {
        this.quizId = quizId;
        this.questionText = questionText;
        this.explanation = explanation;
        this.points = points;
        this.orderIndex = orderIndex;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public Integer getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(Integer orderIndex) {
        this.orderIndex = orderIndex;
    }

    public List<QuizOption> getOptions() {
        return options;
    }

    public void setOptions(List<QuizOption> options) {
        this.options = options;
    }
}
