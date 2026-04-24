package com.alams.controller;

import com.alams.service.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MVC Controller: Quiz management and adaptive quiz flow.
 */
@Controller
@RequestMapping("/quiz")
public class QuizController {

    @Autowired
    private QuizService quizService;

    // === Professor: add quiz question ===
    @PostMapping("/create")
    public String createQuiz(@RequestParam String quizName,
                             @RequestParam String question,
                             @RequestParam String optionA,
                             @RequestParam String optionB,
                             @RequestParam String optionC,
                             @RequestParam String optionD,
                             @RequestParam String correctOption,
                             @RequestParam int difficultyLevel,
                             @RequestParam Long courseId,
                             @RequestParam Long materialId,
                             RedirectAttributes attrs) {
        try {
            quizService.createQuiz(quizName, question, optionA, optionB, optionC, optionD,
                    correctOption, difficultyLevel, materialId);
            attrs.addFlashAttribute("success", "Quiz question added successfully.");
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            attrs.addFlashAttribute("error", "Unable to create quiz right now. Please try again.");
        }
        return "redirect:/courses/" + courseId;
    }

    // === Student: take adaptive quiz ===
    @GetMapping("/take/{materialId}")
    public String takeQuiz(@PathVariable Long materialId,
                           Authentication auth,
                           Model model) {
        try {
            model.addAttribute("quizzes", quizService.getAdaptiveQuizzes(materialId, auth.getName()));
            model.addAttribute("materialId", materialId);
            model.addAttribute("hasPreviousAttempt", quizService.hasSubmittedQuiz(materialId, auth.getName()));
            return "student/take-quiz";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            return "student/take-quiz";
        }
    }

    // === Student: submit an entire quiz set ===
    @PostMapping("/submit")
    public String submitQuiz(@RequestParam Long materialId,
                             @RequestParam Map<String, String> formData,
                             Authentication auth,
                             RedirectAttributes attrs) {
        Map<Long, String> answers = new LinkedHashMap<>();
        formData.forEach((key, value) -> {
            if (key.startsWith("answers[") && key.endsWith("]")) {
                String quizIdText = key.substring("answers[".length(), key.length() - 1);
                answers.put(Long.valueOf(quizIdText), value);
            }
        });

        try {
            Map<String, Object> result = quizService.submitQuizSet(materialId, answers, auth.getName());
            attrs.addFlashAttribute("quizResult", result);
            attrs.addFlashAttribute("resultMessage",
                    "Quiz submitted successfully. You scored " + result.get("score")
                            + " on attempt " + result.get("attemptNumber") + ".");
            return "redirect:/student/results";
        } catch (IllegalArgumentException e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/quiz/take/" + materialId;
        }
    }
}
