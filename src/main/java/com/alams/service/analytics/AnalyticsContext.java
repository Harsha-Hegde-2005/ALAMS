package com.alams.service.analytics;

import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;

/**
 * DESIGN PATTERN: Strategy Pattern - Context Class
 *
 * The AnalyticsContext holds a reference to an AnalyticsStrategy
 * and delegates the report generation to whichever strategy is set at runtime.
 * This allows the reporting algorithm to be switched without changing the client code.
 *
 * DESIGN PRINCIPLE: Dependency Inversion Principle (DIP)
 * AnalyticsContext depends on the AnalyticsStrategy interface (abstraction),
 * not on any concrete strategy class. The actual implementation is injected at runtime.
 */
@Service
public class AnalyticsContext {

    private AnalyticsStrategy strategy;

    public void setStrategy(AnalyticsStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Map<String, Object>> executeReport(String parameter) {
        if (strategy == null) {
            throw new IllegalStateException("Analytics strategy not set.");
        }
        return strategy.generateReport(parameter);
    }

    public String getCurrentStrategyName() {
        return strategy != null ? strategy.getStrategyName() : "None";
    }
}
