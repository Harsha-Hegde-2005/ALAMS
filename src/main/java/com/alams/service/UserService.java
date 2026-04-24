package com.alams.service;

import com.alams.model.User;
import com.alams.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * DESIGN PATTERN: Singleton Pattern
 *
 * Spring @Service beans are singletons by default — only one instance
 * of UserService is created and shared across the entire application context.
 * This is equivalent to the manual Singleton from the original UserService.java,
 * but Spring manages the lifecycle cleanly.
 *
 * DESIGN PRINCIPLE: Single Responsibility Principle (SRP)
 * UserService is solely responsible for user-related business logic:
 * registration, authentication lookup, and user queries.
 * It does NOT handle sessions, views, or other concerns.
 */
@Service  // singleton scope by default
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /** Register a new user (student or professor). */
    public User register(String username, String rawPassword, String fullName, User.Role role) {
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("Username '" + username + "' is already taken.");
        }
        User user = new User(username, passwordEncoder.encode(rawPassword), fullName, role);
        return userRepository.save(user);
    }

    /** Find user by username (used by Spring Security and controllers). */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /** List all students in the system. */
    public List<User> getAllStudents() {
        return userRepository.findByRole(User.Role.STUDENT);
    }

    /** List all professors in the system. */
    public List<User> getAllProfessors() {
        return userRepository.findByRole(User.Role.PROFESSOR);
    }

    /** Count total registered students. */
    public long countStudents() {
        return userRepository.countByRole(User.Role.STUDENT);
    }
}
