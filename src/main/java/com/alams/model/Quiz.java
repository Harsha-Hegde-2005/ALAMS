package com.alams.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Quiz entity - adaptive quiz associated with a material.
 */
@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quiz_name", nullable = false)
    private String quizName;

    @Column(name = "question", nullable = false, length = 1000)
    private String question;

    @Column(name = "option_a", nullable = false)
    private String optionA;

    @Column(name = "option_b", nullable = false)
    private String optionB;

    @Column(name = "option_c", nullable = false)
    private String optionC;

    @Column(name = "option_d", nullable = false)
    private String optionD;

    @Column(name = "correct_option", nullable = false)
    private String correctOption;  // "A", "B", "C", or "D"

    @Column(name = "difficulty_level", nullable = false)
    private int difficultyLevel;  // 1=Easy, 2=Medium, 3=Hard (for adaptive learning)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL)
    private List<QuizSubmission> submissions = new ArrayList<>();

    public Quiz() {}

    public Quiz(String quizName, String question, String optionA, String optionB,
                String optionC, String optionD, String correctOption, int difficultyLevel, Material material) {
        this.quizName = quizName;
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption;
        this.difficultyLevel = difficultyLevel;
        this.material = material;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuizName() { return quizName; }
    public void setQuizName(String quizName) { this.quizName = quizName; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectOption() { return correctOption; }
    public void setCorrectOption(String correctOption) { this.correctOption = correctOption; }

    public int getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(int difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Material getMaterial() { return material; }
    public void setMaterial(Material material) { this.material = material; }

    public List<QuizSubmission> getSubmissions() { return submissions; }
    public void setSubmissions(List<QuizSubmission> submissions) { this.submissions = submissions; }

    public String getDifficultyLabel() {
        return switch (difficultyLevel) {
            case 1 -> "Easy";
            case 2 -> "Medium";
            case 3 -> "Hard";
            default -> "Medium";
        };
    }
}
