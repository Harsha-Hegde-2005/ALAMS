package com.alams.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

@Configuration
public class SchemaMigrationConfig {

    @Bean
    ApplicationRunner cleanupLegacyQuizColumns(JdbcTemplate jdbcTemplate) {
        return args -> {
            List<String> legacyColumns = List.of("optiona", "optionb", "optionc", "optiond");

            for (String column : legacyColumns) {
                Integer count = jdbcTemplate.queryForObject(
                        """
                        SELECT COUNT(*)
                        FROM information_schema.columns
                        WHERE table_schema = DATABASE()
                          AND table_name = 'quizzes'
                          AND column_name = ?
                        """,
                        Integer.class,
                        column
                );

                if (count != null && count > 0) {
                    jdbcTemplate.execute("ALTER TABLE quizzes DROP COLUMN " + column);
                }
            }

            Integer attemptColumn = jdbcTemplate.queryForObject(
                    """
                    SELECT COUNT(*)
                    FROM information_schema.columns
                    WHERE table_schema = DATABASE()
                      AND table_name = 'quiz_submissions'
                      AND column_name = 'attempt_number'
                    """,
                    Integer.class
            );

            if (attemptColumn != null && attemptColumn == 0) {
                jdbcTemplate.execute("ALTER TABLE quiz_submissions ADD COLUMN attempt_number INT NOT NULL DEFAULT 1");
            }
        };
    }
}
