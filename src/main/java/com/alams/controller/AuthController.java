package com.alams.controller;

import com.alams.model.User;
import com.alams.service.CourseService;
import com.alams.service.QuizService;
import com.alams.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * MVC Controller: Authentication & routing
 * DESIGN PRINCIPLE: Single Responsibility - only handles auth and top-level routing.
 */
@Controller
public class AuthController {

    @Autowired private UserService userService;
    @Autowired private CourseService courseService;
    @Autowired private QuizService quizService;

    @GetMapping("/")
    public String home() { return "redirect:/login"; }

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error,
                            @RequestParam(required = false) String logout,
                            Model model) {
        if (error != null) model.addAttribute("error", "Invalid username or password.");
        if (logout != null) model.addAttribute("message", "You have been logged out.");
        return "login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", User.Role.values());
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String fullName,
                           @RequestParam User.Role role,
                           RedirectAttributes attrs) {
        try {
            userService.register(username, password, fullName, role);
            attrs.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (Exception e) {
            attrs.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        User user = userService.findByUsername(auth.getName()).orElseThrow();
        model.addAttribute("user", user);

        if (user.isProfessor()) {
            var myCourses = courseService.getCoursesByProfessor(user);
            model.addAttribute("myCourses", myCourses);
            model.addAttribute("totalCourses", myCourses.size());
            model.addAttribute("totalStudents", quizService.countDistinctStudentsForProfessorCourses(user.getUsername()));
            model.addAttribute("totalSubmissions", quizService.countQuizSubmissionsForProfessorCourses(user.getUsername()));
            return "professor/dashboard";
        } else {
            model.addAttribute("totalCourses", courseService.countCourses());
            model.addAttribute("allCourses", courseService.getAllCourses());
            model.addAttribute("studentSubmissions", quizService.countQuizSubmissionsForStudent(user.getUsername()));
            return "student/dashboard";
        }
    }
}
