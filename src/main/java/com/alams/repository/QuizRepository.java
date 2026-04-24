package com.alams.repository;

import com.alams.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByMaterialId(Long materialId);
    List<Quiz> findByMaterialIdAndDifficultyLevel(Long materialId, int difficultyLevel);
    List<Quiz> findByMaterialIdAndDifficultyLevelOrderByQuizNameAscIdAsc(Long materialId, int difficultyLevel);
    List<Quiz> findByMaterialIdOrderByDifficultyLevelAscQuizNameAscIdAsc(Long materialId);
}
