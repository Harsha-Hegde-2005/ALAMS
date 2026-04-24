package com.alams.service.analytics;

import com.alams.model.QuizSubmission;
import com.alams.repository.QuizSubmissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Concrete Strategy: Course-level Performance Analytics
 * Reports average scores of all students in a specific course.
 */
@Component("coursePerformanceStrategy")
public class CoursePerformanceStrategy implements AnalyticsStrategy {

    @Autowired
    private QuizSubmissionRepository submissionRepository;

    @Autowired
    private AdaptiveAnalyticsSupport analyticsSupport;

    @Override
    public List<Map<String, Object>> generateReport(String courseName) {
        List<Map<String, Object>> report = new ArrayList<>();

        List<QuizSubmission> submissions = submissionRepository.findByCourseName(courseName);

        // Group by student
        Map<String, List<QuizSubmission>> grouped = new LinkedHashMap<>();
        for (QuizSubmission sub : submissions) {
            String key = sub.getStudent().getUsername();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(sub);
        }

        for (Map.Entry<String, List<QuizSubmission>> entry : grouped.entrySet()) {
            List<QuizSubmission> subs = entry.getValue();
            Map<Long, List<QuizSubmission>> byMaterial = new LinkedHashMap<>();
            for (QuizSubmission submission : subs) {
                byMaterial.computeIfAbsent(submission.getQuiz().getMaterial().getId(), key -> new ArrayList<>()).add(submission);
            }

            List<QuizSubmission> latestAcrossMaterials = new ArrayList<>();
            int totalRetakes = 0;
            for (List<QuizSubmission> materialSubs : byMaterial.values()) {
                latestAcrossMaterials.addAll(analyticsSupport.latestAttempt(materialSubs));
                totalRetakes += analyticsSupport.retakeCount(materialSubs);
            }

            long correct = latestAcrossMaterials.stream().filter(QuizSubmission::isCorrect).count();
            long total = latestAcrossMaterials.size();
            double pct = total > 0 ? (double) correct / total * 100.0 : 0;
            double avgDifficulty = analyticsSupport.averageDifficulty(latestAcrossMaterials);

            Map<String, Object> row = new LinkedHashMap<>();
            row.put("student", entry.getKey());
            row.put("correct", correct);
            row.put("total", total);
            row.put("percentage", String.format("%.1f%%", pct));
            row.put("badge", analyticsSupport.badgeFor(pct));
            row.put("effectiveness", analyticsSupport.effectivenessLabel(pct));
            row.put("adaptiveness", analyticsSupport.adaptivenessLabel(pct, avgDifficulty));
            row.put("suggestion", analyticsSupport.suggestionFor(pct, avgDifficulty));
            row.put("averageDifficulty", String.format("%.1f", avgDifficulty));
            row.put("materialsCovered", byMaterial.size());
            row.put("retakeCount", totalRetakes);
            report.add(row);
        }

        return report;
    }

    @Override
    public String getStrategyName() {
        return "Course Performance";
    }
}
