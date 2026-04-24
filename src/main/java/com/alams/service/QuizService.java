package com.alams.service;

import com.alams.events.QuizSubmittedEvent;
import com.alams.model.Material;
import com.alams.model.Quiz;
import com.alams.model.QuizSubmission;
import com.alams.model.User;
import com.alams.repository.MaterialRepository;
import com.alams.repository.QuizRepository;
import com.alams.repository.QuizSubmissionRepository;
import com.alams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Quiz management service.
 * DESIGN PATTERN: Observer Pattern (event publishing side)
 * After a quiz is graded, QuizSubmittedEvent is published so all
 * registered listeners (observers) are notified automatically.
 */
@Service
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuizSubmissionRepository submissionRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    public Quiz createQuiz(String quizName, String question,
                           String optA, String optB, String optC, String optD,
                           String correctOption, int difficulty, Long materialId) {
        if (materialId == null || materialId <= 0) {
            throw new IllegalArgumentException("Invalid material ID");
        }

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        Quiz quiz = new Quiz(quizName, question, optA, optB, optC, optD, correctOption, difficulty, material);
        return quizRepository.save(quiz);
    }

    public List<Quiz> getQuizzesByMaterial(Long materialId) {
        return quizRepository.findByMaterialId(materialId);
    }

    public boolean hasSubmittedQuiz(Long materialId, String username) {
        return !submissionRepository.findByStudentUsernameAndMaterialId(username, materialId).isEmpty();
    }

    public List<Quiz> getAdaptiveQuizzes(Long materialId, String username) {
        List<Quiz> allQuizzes = quizRepository.findByMaterialIdOrderByDifficultyLevelAscQuizNameAscIdAsc(materialId);
        if (allQuizzes.isEmpty()) {
            return allQuizzes;
        }

        List<QuizSubmission> history = submissionRepository.findByStudentUsernameAndMaterialId(username, materialId);
        if (history.isEmpty()) {
            return uniqueQuestions(allQuizzes);
        }

        List<QuizSubmission> latestAttempt = getLatestAttemptSubmissions(history);
        long correct = latestAttempt.stream().filter(QuizSubmission::isCorrect).count();
        double pct = latestAttempt.isEmpty() ? 0.0 : (double) correct / latestAttempt.size() * 100.0;

        int targetDifficulty = 2;
        if (pct >= 80) {
            targetDifficulty = 3;
        } else if (pct < 50) {
            targetDifficulty = 1;
        }

        Set<Long> seenQuizIds = new LinkedHashSet<>();
        for (QuizSubmission submission : history) {
            seenQuizIds.add(submission.getQuiz().getId());
        }

        List<Quiz> preferred = uniqueQuestions(selectQuizzes(allQuizzes, targetDifficulty, seenQuizIds, true));
        if (!preferred.isEmpty()) {
            return preferred;
        }

        List<Quiz> unseenAnyDifficulty = uniqueQuestions(allQuizzes.stream()
                .filter(quiz -> !seenQuizIds.contains(quiz.getId()))
                .toList());
        if (!unseenAnyDifficulty.isEmpty()) {
            return unseenAnyDifficulty;
        }

        return List.of();
    }

    @Transactional
    public Map<String, Object> submitQuizSet(Long materialId, Map<Long, String> answers, String username) {
        if (answers == null || answers.isEmpty()) {
            throw new IllegalArgumentException("Please answer all questions before submitting.");
        }

        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        List<Quiz> quizzes = quizRepository.findAllById(answers.keySet()).stream()
                .filter(quiz -> quiz.getMaterial() != null && materialId.equals(quiz.getMaterial().getId()))
                .sorted(Comparator.comparingInt(Quiz::getDifficultyLevel)
                        .thenComparing(Quiz::getQuizName)
                        .thenComparing(Quiz::getId))
                .toList();

        if (quizzes.size() != answers.size()) {
            throw new IllegalArgumentException("Some quiz questions could not be verified. Please retry the quiz.");
        }

        int attemptNumber = nextAttemptNumber(username, materialId);
        List<QuizSubmission> submissions = new ArrayList<>();
        List<Map<String, Object>> reviewItems = new ArrayList<>();
        int totalCorrect = 0;

        for (Quiz quiz : quizzes) {
            String selectedOption = answers.get(quiz.getId());
            if (selectedOption == null || selectedOption.isBlank()) {
                throw new IllegalArgumentException("Please answer all questions before submitting.");
            }

            boolean correct = quiz.getCorrectOption().equalsIgnoreCase(selectedOption);
            if (correct) {
                totalCorrect++;
            }

            submissions.add(new QuizSubmission(quiz, student, selectedOption, correct, attemptNumber));

            Map<String, Object> reviewItem = new LinkedHashMap<>();
            reviewItem.put("question", quiz.getQuestion());
            reviewItem.put("difficulty", quiz.getDifficultyLabel());
            reviewItem.put("selectedOption", selectedOption.toUpperCase());
            reviewItem.put("correctOption", quiz.getCorrectOption());
            reviewItem.put("selectedText", getOptionText(quiz, selectedOption));
            reviewItem.put("correctText", getOptionText(quiz, quiz.getCorrectOption()));
            reviewItem.put("correct", correct);
            reviewItems.add(reviewItem);
        }

        submissionRepository.saveAll(submissions);

        int totalQuestions = submissions.size();
        double percentage = totalQuestions > 0 ? (double) totalCorrect / totalQuestions * 100.0 : 0.0;

        eventPublisher.publishEvent(new QuizSubmittedEvent(
                this,
                username,
                material.getCourse().getCourseName(),
                materialId,
                totalCorrect,
                totalQuestions
        ));

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("correctAnswers", totalCorrect);
        result.put("totalQuestions", totalQuestions);
        result.put("score", totalCorrect + "/" + totalQuestions);
        result.put("percentage", String.format("%.1f", percentage));
        result.put("materialId", materialId);
        result.put("quizName", quizzes.isEmpty() ? "Adaptive Quiz" : quizzes.get(0).getQuizName());
        result.put("reviewItems", reviewItems);
        result.put("attemptNumber", attemptNumber);
        result.put("retakeCount", Math.max(0, attemptNumber - 1));
        return result;
    }

    public long countTotalQuizSubmissions() {
        return submissionRepository.count();
    }

    public long countQuizSubmissionsForStudent(String username) {
        return submissionRepository.countByStudentUsername(username);
    }

    public long countQuizSubmissionsForProfessorCourses(String username) {
        return submissionRepository.countByProfessorUsername(username);
    }

    public long countDistinctStudentsForProfessorCourses(String username) {
        return submissionRepository.findDistinctStudentsByProfessorUsername(username).size();
    }

    public List<User> getStudentsForProfessorCourses(String username) {
        return submissionRepository.findDistinctStudentsByProfessorUsername(username);
    }

    private List<Quiz> selectQuizzes(List<Quiz> allQuizzes, int targetDifficulty, Set<Long> seenQuizIds, boolean onlyUnseen) {
        return allQuizzes.stream()
                .filter(quiz -> quiz.getDifficultyLevel() == targetDifficulty)
                .filter(quiz -> !onlyUnseen || !seenQuizIds.contains(quiz.getId()))
                .toList();
    }

    private List<Quiz> uniqueQuestions(List<Quiz> quizzes) {
        List<Quiz> unique = new ArrayList<>();
        Set<String> seenQuestions = new LinkedHashSet<>();

        for (Quiz quiz : quizzes) {
            String normalizedQuestion = quiz.getQuestion() == null
                    ? ""
                    : quiz.getQuestion().trim().toLowerCase(Locale.ROOT);
            if (seenQuestions.add(normalizedQuestion)) {
                unique.add(quiz);
            }
        }

        return unique;
    }

    private List<QuizSubmission> getLatestAttemptSubmissions(List<QuizSubmission> submissions) {
        int latestAttempt = submissions.stream()
                .mapToInt(QuizSubmission::getAttemptNumber)
                .max()
                .orElse(1);

        return submissions.stream()
                .filter(submission -> submission.getAttemptNumber() == latestAttempt)
                .toList();
    }

    private int nextAttemptNumber(String username, Long materialId) {
        return submissionRepository.findByStudentUsernameAndMaterialId(username, materialId).stream()
                .mapToInt(QuizSubmission::getAttemptNumber)
                .max()
                .orElse(0) + 1;
    }

    private String getOptionText(Quiz quiz, String optionKey) {
        return switch (optionKey.toUpperCase()) {
            case "A" -> quiz.getOptionA();
            case "B" -> quiz.getOptionB();
            case "C" -> quiz.getOptionC();
            case "D" -> quiz.getOptionD();
            default -> "";
        };
    }
}
