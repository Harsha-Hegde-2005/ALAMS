package com.alams;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for Adaptive Learning and Assessment Management System (ALAMS)
 *
 * Architecture: MVC (Spring Boot)
 * Design Patterns used:
 *   1. Singleton     - UserService (Spring @Service is a singleton by default)
 *   2. Factory Method - MaterialFactory hierarchy for creating learning materials
 *   3. Observer      - QuizEventPublisher / QuizEventListener (Spring Events)
 *   4. Strategy      - AnalyticsStrategy for pluggable performance reports
 *
 * Design Principles:
 *   1. SRP  - Each service class has a single responsibility
 *   2. OCP  - MaterialFactory is open for extension, closed for modification
 *   3. LSP  - All analytics strategies are substitutable
 *   4. DIP  - Controllers depend on service interfaces, not concrete classes
 */
@SpringBootApplication
public class AlamApplication {
    public static void main(String[] args) {
        SpringApplication.run(AlamApplication.class, args);
    }
}
