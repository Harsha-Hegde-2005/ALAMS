-- =====================================================
-- ALAMS - Adaptive Learning & Assessment Management System
-- Database Schema
-- =====================================================

CREATE DATABASE IF NOT EXISTS alams_db;
USE alams_db;

-- Users Table (Students and Professors)
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(200) NOT NULL,
    role ENUM('STUDENT', 'PROFESSOR') NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- Courses Table
CREATE TABLE IF NOT EXISTS courses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    course_name VARCHAR(200) NOT NULL,
    description TEXT,
    professor_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (professor_id) REFERENCES users(id)
);

-- Materials Table (Factory Method creates these)
CREATE TABLE IF NOT EXISTS materials (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    file_name VARCHAR(300) NOT NULL,
    file_path VARCHAR(500) NOT NULL,
    material_type ENUM('PDF', 'PRESENTATION', 'DOCUMENT', 'GENERIC') NOT NULL,
    course_id BIGINT,
    uploaded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (course_id) REFERENCES courses(id) ON DELETE CASCADE
);

-- Quizzes Table (with adaptive difficulty level)
CREATE TABLE IF NOT EXISTS quizzes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_name VARCHAR(200) NOT NULL,
    question TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option CHAR(1) NOT NULL,
    difficulty_level INT DEFAULT 2 CHECK (difficulty_level IN (1, 2, 3)),
    material_id BIGINT,
    FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE
);

-- Quiz Submissions (Observer pattern logs here)
CREATE TABLE IF NOT EXISTS quiz_submissions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    quiz_id BIGINT,
    student_id BIGINT,
    selected_option CHAR(1) NOT NULL,
    is_correct BOOLEAN NOT NULL,
    submitted_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (quiz_id) REFERENCES quizzes(id),
    FOREIGN KEY (student_id) REFERENCES users(id)
);

