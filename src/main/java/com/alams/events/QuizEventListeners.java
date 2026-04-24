package com.alams.events;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * DESIGN PATTERN: Observer Pattern - Concrete Observers
 *
 * These listeners are decoupled from the quiz submission logic.
 * Spring's event system acts as the Subject (publisher), and each
 * @EventListener-annotated method is a concrete Observer.
 */

/**
 * Observer 1: Logs quiz submission activity for audit trail.
 */
@Component
class ScoreAuditListener {

    private static final Logger log = LoggerFactory.getLogger(ScoreAuditListener.class);

    @EventListener
    public void onQuizSubmitted(QuizSubmittedEvent event) {
        log.info("[AUDIT] Quiz submitted: {}", event.getSummary());
    }
}

/**
 * Observer 2: Notifies the professor when a student completes a quiz.
 * In production, this would send an email or push notification.
 */
@Component
class ProfessorNotificationListener {

    private static final Logger log = LoggerFactory.getLogger(ProfessorNotificationListener.class);

    @EventListener
    public void onQuizSubmitted(QuizSubmittedEvent event) {
        log.info("[NOTIFY] Professor notified: student '{}' completed quiz in course '{}'",
                event.getStudentUsername(), event.getCourseName());
        // In production: emailService.sendToCourseProfessor(event);
    }
}

/**
 * Observer 3: Triggers adaptive difficulty adjustment based on score.
 * If a student scores ≥ 80%, they get harder quizzes next time.
 */
@Component
class AdaptiveDifficultyListener {

    private static final Logger log = LoggerFactory.getLogger(AdaptiveDifficultyListener.class);

    @EventListener
    public void onQuizSubmitted(QuizSubmittedEvent event) {
        double pct = event.getPercentage();
        String recommendation;
        if (pct >= 80) {
            recommendation = "INCREASE difficulty (score ≥ 80%)";
        } else if (pct >= 50) {
            recommendation = "MAINTAIN difficulty (score 50-79%)";
        } else {
            recommendation = "DECREASE difficulty (score < 50%)";
        }
        log.info("[ADAPTIVE] Student '{}' → {}", event.getStudentUsername(), recommendation);
    }
}
