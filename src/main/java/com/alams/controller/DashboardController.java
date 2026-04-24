package com.alams.controller;

import com.alams.model.Course;
import com.alams.model.User;
import com.alams.service.CourseService;
import com.alams.service.QuizService;
import com.alams.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/dashboard")
public class DashboardController {

    @Autowired
    private UserService userService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private QuizService quizService;

    @GetMapping("/professor/courses")
    public String professorCourses(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        List<Course> allCourses = courseService.getCoursesByProfessor(user);
        model.addAttribute("user", user);
        model.addAttribute("title", "Total Courses");
        model.addAttribute("description", "Courses currently managed by you.");
        model.addAttribute("courses", allCourses);
        return "professor/dashboard-courses";
    }

    @GetMapping("/professor/students")
    public String professorStudents(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("title", "Registered Students");
        model.addAttribute("description", "Students who have attempted quizzes in your courses.");
        model.addAttribute("students", quizService.getStudentsForProfessorCourses(user.getUsername()));
        return "professor/dashboard-students";
    }

    @GetMapping("/professor/my-courses")
    public String professorMyCourses(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("title", "My Courses");
        model.addAttribute("description", "Courses created and managed by you.");
        model.addAttribute("courses", courseService.getCoursesByProfessor(user));
        return "professor/dashboard-courses";
    }

    @GetMapping("/professor/submissions")
    public String professorSubmissions(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("title", "Quiz Submissions");
        model.addAttribute("description", "Quiz submissions recorded for your courses only.");
        model.addAttribute("metricLabel", "Total Submissions");
        model.addAttribute("metricValue", quizService.countQuizSubmissionsForProfessorCourses(user.getUsername()));
        return "professor/dashboard-metric";
    }

    @GetMapping("/student/courses")
    public String studentCourses(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("title", "Available Courses");
        model.addAttribute("description", "Browse all courses you can open and study.");
        model.addAttribute("courses", courseService.getAllCourses());
        return "student/dashboard-courses";
    }

    @GetMapping("/student/submissions")
    public String studentSubmissions(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("title", "Quiz Submissions");
        model.addAttribute("description", "Your quiz activity summary.");
        model.addAttribute("metricLabel", "Your Submission Count");
        model.addAttribute("metricValue", quizService.countQuizSubmissionsForStudent(user.getUsername()));
        return "student/dashboard-metric";
    }
}
