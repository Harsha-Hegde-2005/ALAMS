package com.alams.service.analytics;

import java.util.List;
import java.util.Map;

/**
 * DESIGN PATTERN: Strategy Pattern
 *
 * Interface defining the contract for all analytics strategies.
 * Allows the system to select a reporting algorithm at runtime.
 *
 * DESIGN PRINCIPLE: Interface Segregation Principle (ISP)
 * Clients only depend on the methods they need. Each strategy
 * implements only the generateReport method relevant to its concern.
 *
 * DESIGN PRINCIPLE: Liskov Substitution Principle (LSP)
 * Any concrete strategy (Student/Course/Material) can replace
 * the interface reference without breaking functionality.
 */
public interface AnalyticsStrategy {
    /**
     * Generate a performance report for a given parameter.
     * @param parameter - username for student strategy, courseName for course strategy
     * @return list of report rows as key-value maps
     */
    List<Map<String, Object>> generateReport(String parameter);

    /** Human-readable name of this strategy */
    String getStrategyName();
}
