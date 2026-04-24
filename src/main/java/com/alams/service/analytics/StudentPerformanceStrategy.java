package com.alams.service.analytics;

import com.alams.model.QuizSubmission;
import com.alams.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Concrete Strategy: Student Performance Analytics
 * Reports how a specific student is performing across all quizzes.
 */
@Component("studentPerformanceStrategy")
public class StudentPerformanceStrategy implements AnalyticsStrategy {

    @Autowired
    private QuizSubmissionRepository submissionRepository;

    @Autowired
    private AdaptiveAnalyticsSupport analyticsSupport;

    @Override
    public List<Map<String, Object>> generateReport(String username) {
        List<Map<String, Object>> report = new ArrayList<>();

        List<QuizSubmission> submissions = submissionRepository.findByStudentUsername(username);

        // Group by course + material
        Map<String, List<QuizSubmission>> grouped = new LinkedHashMap<>();
        for (QuizSubmission sub : submissions) {
            String key = sub.getQuiz().getMaterial().getCourse().getCourseName()
                    + " | " + sub.getQuiz().getMaterial().getFileName();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(sub);
        }

        for (Map.Entry<String, List<QuizSubmission>> entry : grouped.entrySet()) {
            String[] parts = entry.getKey().split(" \\| ", 2);
            List<QuizSubmission> subs = entry.getValue();
            List<QuizSubmission> latestAttempt = analyticsSupport.latestAttempt(subs);
            long correct = latestAttempt.stream().filter(QuizSubmission::isCorrect).count();
            long total = latestAttempt.size();
            double pct = total > 0 ? (double) correct / total * 100.0 : 0;
            double avgDifficulty = analyticsSupport.averageDifficulty(latestAttempt);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("course", parts[0]);
            row.put("material", parts.length > 1 ? parts[1] : "");
            row.put("correct", correct);
            row.put("total", total);
            row.put("percentage", String.format("%.1f%%", pct));
            row.put("badge", analyticsSupport.badgeFor(pct));
            row.put("effectiveness", analyticsSupport.effectivenessLabel(pct));
            row.put("adaptiveness", analyticsSupport.adaptivenessLabel(pct, avgDifficulty));
            row.put("suggestion", analyticsSupport.suggestionFor(pct, avgDifficulty));
            row.put("averageDifficulty", String.format("%.1f", avgDifficulty));
            row.put("retakeCount", analyticsSupport.retakeCount(subs));
            report.add(row);
        }

        return report;
    }

    @Override
    public String getStrategyName() {
        return "Student Performance";
    }
}
