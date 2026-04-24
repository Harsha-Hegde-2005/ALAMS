package com.alams.service.analytics;

import com.alams.model.QuizSubmission;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class AdaptiveAnalyticsSupport {

    public String badgeFor(double percentage) {
        if (percentage >= 80) {
            return "success";
        }
        if (percentage >= 50) {
            return "warning";
        }
        return "danger";
    }

    public String effectivenessLabel(double percentage) {
        if (percentage >= 85) {
            return "Highly Effective";
        }
        if (percentage >= 65) {
            return "Effective";
        }
        if (percentage >= 50) {
            return "Developing";
        }
        return "Needs Support";
    }

    public String adaptivenessLabel(double percentage, double averageDifficulty) {
        if (percentage >= 80 && averageDifficulty >= 2.5) {
            return "Advanced Adaptive";
        }
        if (percentage >= 65 && averageDifficulty >= 2.0) {
            return "Well Aligned";
        }
        if (percentage >= 50) {
            return "Moderately Adaptive";
        }
        return "Needs Rebalancing";
    }

    public String suggestionFor(double percentage, double averageDifficulty) {
        if (percentage >= 85 && averageDifficulty >= 2.5) {
            return "Keep challenging this learner with harder case-based questions.";
        }
        if (percentage >= 75) {
            return "Sustain the current pace and add a few harder adaptive questions.";
        }
        if (percentage >= 50) {
            return "Revisit core concepts once, then retry with medium-difficulty questions.";
        }
        return "Start with simpler study material review before attempting the next quiz set.";
    }

    public double averageDifficulty(List<QuizSubmission> submissions) {
        return submissions.stream()
                .mapToInt(submission -> submission.getQuiz().getDifficultyLevel())
                .average()
                .orElse(0.0);
    }

    public int materialsCovered(List<QuizSubmission> submissions) {
        Set<Long> materialIds = new LinkedHashSet<>();
        for (QuizSubmission submission : submissions) {
            materialIds.add(submission.getQuiz().getMaterial().getId());
        }
        return materialIds.size();
    }

    public List<QuizSubmission> latestAttempt(List<QuizSubmission> submissions) {
        int latestAttempt = submissions.stream()
                .mapToInt(QuizSubmission::getAttemptNumber)
                .max()
                .orElse(1);

        return submissions.stream()
                .filter(submission -> submission.getAttemptNumber() == latestAttempt)
                .toList();
    }

    public int retakeCount(List<QuizSubmission> submissions) {
        int latestAttempt = submissions.stream()
                .mapToInt(QuizSubmission::getAttemptNumber)
                .max()
                .orElse(1);
        return Math.max(0, latestAttempt - 1);
    }
}
