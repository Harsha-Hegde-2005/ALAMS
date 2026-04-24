package com.alams.controller;

import com.alams.service.analytics.AnalyticsContext;
import com.alams.service.analytics.CoursePerformanceStrategy;
import com.alams.service.analytics.StudentPerformanceStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * MVC Controller: Analytics and performance reports.
 * Demonstrates the Strategy Pattern in action — the controller
 * selects the appropriate strategy at request time.
 */
@Controller
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired private AnalyticsContext analyticsContext;
    @Autowired private StudentPerformanceStrategy studentStrategy;
    @Autowired private CoursePerformanceStrategy courseStrategy;

    /** Student views their own performance report */
    @GetMapping("/student")
    public String studentAnalytics(Authentication auth, Model model) {
        analyticsContext.setStrategy(studentStrategy);   // Strategy pattern: set algorithm
        model.addAttribute("report", analyticsContext.executeReport(auth.getName()));
        model.addAttribute("strategyName", analyticsContext.getCurrentStrategyName());
        model.addAttribute("username", auth.getName());
        return "student/analytics";
    }

    /** Professor views course-wide performance */
    @GetMapping("/course")
    public String courseAnalytics(@RequestParam String courseName, Model model) {
        analyticsContext.setStrategy(courseStrategy);    // Strategy pattern: switch algorithm
        model.addAttribute("report", analyticsContext.executeReport(courseName));
        model.addAttribute("strategyName", analyticsContext.getCurrentStrategyName());
        model.addAttribute("courseName", courseName);
        return "professor/analytics";
    }
}
