package com.alams.events;

import com.alams.model.QuizSubmission;
import org.springframework.context.ApplicationEvent;

/**
 * DESIGN PATTERN: Observer Pattern (via Spring ApplicationEvent)
 *
 * QuizSubmittedEvent is published whenever a student submits a quiz.
 * Multiple listeners (observers) can react independently:
 *   - ScoreRecorderListener  → saves the submission to the database
 *   - NotificationListener   → notifies the professor of student activity
 *
 * DESIGN PRINCIPLE: Single Responsibility Principle (SRP)
 * The event class only carries the event data. Each listener handles its own reaction.
 */
public class QuizSubmittedEvent extends ApplicationEvent {

    private final String studentUsername;
    private final String courseName;
    private final Long materialId;
    private final int correctAnswers;
    private final int totalQuestions;

    public QuizSubmittedEvent(Object source, String studentUsername, String courseName,
                              Long materialId, int correctAnswers, int totalQuestions) {
        super(source);
        this.studentUsername = studentUsername;
        this.courseName = courseName;
        this.materialId = materialId;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
    }

    public String getStudentUsername() { return studentUsername; }
    public String getCourseName() { return courseName; }
    public Long getMaterialId() { return materialId; }
    public int getCorrectAnswers() { return correctAnswers; }
    public int getTotalQuestions() { return totalQuestions; }

    public double getPercentage() {
        return totalQuestions > 0 ? (double) correctAnswers / totalQuestions * 100.0 : 0;
    }

    public String getSummary() {
        return String.format("%s scored %d/%d (%.1f%%) on %s",
                studentUsername, correctAnswers, totalQuestions, getPercentage(), courseName);
    }
}
