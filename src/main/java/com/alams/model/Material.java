package com.alams.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Material entity - represents a learning resource uploaded to a course.
 * Supports PDF, PPT, DOCX, and generic file types.
 */
@Entity
@Table(name = "materials")
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MaterialType materialType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "uploaded_at")
    private LocalDateTime uploadedAt;

    public enum MaterialType {
        PDF, PRESENTATION, DOCUMENT, GENERIC
    }

    public Material() {}

    public Material(String fileName, String filePath, MaterialType materialType, Course course) {
        this.fileName = fileName;
        this.filePath = filePath;
        this.materialType = materialType;
        this.course = course;
        this.uploadedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public MaterialType getMaterialType() { return materialType; }
    public void setMaterialType(MaterialType materialType) { this.materialType = materialType; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public LocalDateTime getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(LocalDateTime uploadedAt) { this.uploadedAt = uploadedAt; }

    public String getTypeIcon() {
        return switch (materialType) {
            case PDF -> "📄";
            case PRESENTATION -> "📊";
            case DOCUMENT -> "📝";
            default -> "📁";
        };
    }
}
