package com.alams.service;

import com.alams.model.Course;
import com.alams.model.Material;
import com.alams.model.User;
import com.alams.repository.CourseRepository;
import com.alams.repository.MaterialRepository;
import com.alams.service.material.MaterialFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for course and material management.
 * Uses the Factory Method pattern via MaterialFactory.
 */
@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private MaterialRepository materialRepository;

    @Value("${alams.upload.dir:uploads}")
    private String uploadDir;

    public Course createCourse(String courseName, String description, User professor) {
        Course course = new Course(courseName, description, professor);
        return courseRepository.save(course);
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<Course> getCoursesByProfessor(User professor) {
        return courseRepository.findByProfessor(professor);
    }

    public Optional<Course> findById(Long id) {
        return courseRepository.findById(id);
    }

    public Optional<Course> findByName(String name) {
        return courseRepository.findByCourseName(name);
    }

    /**
     * Uploads a learning material to the given course.
     * Uses the Factory Method pattern to create the appropriate Material type.
     */
    public Material uploadMaterial(MultipartFile file, Long courseId) throws IOException {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("Course not found: " + courseId));

        // Store file on disk
        Path uploadPath = Paths.get(uploadDir, "course_" + courseId);
        Files.createDirectories(uploadPath);
        String uniqueName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path dest = uploadPath.resolve(uniqueName);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // Factory Method pattern: select factory based on file type
        MaterialFactory factory = MaterialFactory.getFactory(file);
        Material material = factory.createMaterial(file, course, dest.toString());

        return materialRepository.save(material);
    }

    public List<Material> getMaterialsByCourse(Long courseId) {
        return materialRepository.findByCourseId(courseId);
    }

    public Optional<Material> findMaterialById(Long materialId) {
        return materialRepository.findById(materialId);
    }

    public Path getUploadRoot() {
        return Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public long countCourses() {
        return courseRepository.count();
    }
}
