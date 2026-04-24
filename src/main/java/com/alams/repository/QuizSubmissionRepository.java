package com.alams.repository;

import com.alams.model.QuizSubmission;
import com.alams.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuizSubmissionRepository extends JpaRepository<QuizSubmission, Long> {

    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.student.username = :username")
    List<QuizSubmission> findByStudentUsername(@Param("username") String username);

    long countByStudentUsername(String username);

    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.material.course.courseName = :name")
    List<QuizSubmission> findByCourseName(@Param("name") String courseName);

    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.quiz.material.course.professor.username = :username")
    List<QuizSubmission> findByProfessorUsername(@Param("username") String username);

    @Query("SELECT COUNT(qs) FROM QuizSubmission qs WHERE qs.quiz.material.course.professor.username = :username")
    long countByProfessorUsername(@Param("username") String username);

    @Query("SELECT DISTINCT qs.student FROM QuizSubmission qs WHERE qs.quiz.material.course.professor.username = :username ORDER BY qs.student.fullName")
    List<User> findDistinctStudentsByProfessorUsername(@Param("username") String username);

    @Query("SELECT qs FROM QuizSubmission qs WHERE qs.student.username = :username AND qs.quiz.material.id = :mid")
    List<QuizSubmission> findByStudentUsernameAndMaterialId(
            @Param("username") String username,
            @Param("mid") Long materialId);
}
