package com.alams.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * QuizSubmission entity - records a student's answer to a quiz question.
 */
@Entity
@Table(name = "quiz_submissions")
public class QuizSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User student;

    @Column(nullable = false)
    private String selectedOption;

    @Column(nullable = false)
    private boolean isCorrect;

    @Column(name = "attempt_number", nullable = false)
    private int attemptNumber;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public QuizSubmission() {}

    public QuizSubmission(Quiz quiz, User student, String selectedOption, boolean isCorrect, int attemptNumber) {
        this.quiz = quiz;
        this.student = student;
        this.selectedOption = selectedOption;
        this.isCorrect = isCorrect;
        this.attemptNumber = attemptNumber;
        this.submittedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Quiz getQuiz() { return quiz; }
    public void setQuiz(Quiz quiz) { this.quiz = quiz; }

    public User getStudent() { return student; }
    public void setStudent(User student) { this.student = student; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public boolean isCorrect() { return isCorrect; }
    public void setCorrect(boolean correct) { isCorrect = correct; }

    public int getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(int attemptNumber) { this.attemptNumber = attemptNumber; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
