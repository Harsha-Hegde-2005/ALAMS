package com.alams.repository;

import com.alams.model.Course;
import com.alams.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByProfessor(User professor);
    Optional<Course> findByCourseName(String courseName);
}