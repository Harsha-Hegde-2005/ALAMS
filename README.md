# 🎓 Adaptive Learning and Assessment Management System (ALAMS)

## UE23CS352B – Object Oriented Analysis & Design | Mini Project

**Team:** Harsha Madev Hegde, H Achyuth, Gowni Ananya, Harshavardhan N, Gudihalli Kiran  
**Guide:** Prof. Sowmya A M  
**Tech Stack:** Java 17, Spring Boot 3.2, Spring MVC, Thymeleaf, Spring Security, MySQL

---

## 🏗️ Architecture: MVC (Spring Boot)

| Layer | Technology | Purpose |
|-------|-----------|---------|
| **Model** | JPA Entities | `User`, `Course`, `Material`, `Quiz`, `QuizSubmission` |
| **View** | Thymeleaf HTML + CSS | Login, Dashboards, Quiz, Analytics |
| **Controller** | Spring `@Controller` | `AuthController`, `CourseController`, `QuizController`, `AnalyticsController` |

---

## 🎨 Design Patterns

| Pattern | Category | Class(es) | Owner |
|---------|----------|-----------|-------|
| **Singleton** | Creational | `UserService` (`@Service`) | Harsha Madev Hegde |
| **Factory Method** | Creational/Structural | `MaterialFactory`, `PdfMaterialFactory`, `PresentationMaterialFactory`, `DocumentMaterialFactory`, `GenericMaterialFactory` | H Achyuth |
| **Observer** | Behavioral | `QuizSubmittedEvent`, `ScoreAuditListener`, `ProfessorNotificationListener`, `AdaptiveDifficultyListener` | Gowni Ananya |
| **Strategy** | Behavioral | `AnalyticsStrategy`, `StudentPerformanceStrategy`, `CoursePerformanceStrategy`, `AnalyticsContext` | Harshavardhan N |


---

## 📐 Design Principles

| Principle | Applied In |
|-----------|-----------|
| **SRP** – Single Responsibility | Each service class handles one concern only |
| **OCP** – Open/Closed | `MaterialFactory` open for new types, closed for modification |
| **LSP** – Liskov Substitution | All `AnalyticsStrategy` implementations are substitutable |
| **DIP** – Dependency Inversion | Controllers depend on service interfaces, not concretions |

---

## 🚀 How to Run

### Prerequisites
- Java 17+
- Maven 3.8+
- MySQL 8.0+

### Steps

```bash
# 1. Create database
mysql -u root -p < src/main/resources/schema.sql

# 2. Update DB credentials in application.properties
# spring.datasource.username=root
# spring.datasource.password=your_password

# 3. Build and Run
mvn spring-boot:run

# 4. Open browser
# http://localhost:8080
```

### Default Login (after schema.sql)
| Username | Password | Role |
|----------|----------|------|
| `prof.sowmya` | `password123` | Professor |
| `harsha.madev` | `password123` | Student |

---

## 📁 Project Structure

```
src/main/java/com/alams/
├── AlamApplication.java          ← Main entry point
├── config/
│   └── SecurityConfig.java       ← Spring Security (roles)
├── controller/
│   ├── AuthController.java       ← Login/Register/Dashboard
│   ├── CourseController.java     ← Course + Material upload
│   ├── QuizController.java       ← Quiz CRUD + submission
│   ├── AnalyticsController.java  ← Strategy pattern usage
│   └── StudentController.java   
├── model/                        ← JPA Entities (MVC: Model)
├── repository/                   ← Spring Data JPA
├── service/
│   ├── UserService.java          ← Singleton pattern
│   ├── CourseService.java        ← Uses Factory Method
│   ├── QuizService.java          ← Publishes Observer events
│   ├── material/
│   │   ├── MaterialFactory.java  ← Factory Method (abstract)
│   │   └── ConcreteFactories.java
│   └── analytics/
│       ├── AnalyticsStrategy.java ← Strategy interface
│       ├── StudentPerformanceStrategy.java
│       ├── CoursePerformanceStrategy.java
│       └── AnalyticsContext.java
└── events/
    ├── QuizSubmittedEvent.java   ← Observer event
    └── QuizEventListeners.java  ← Concrete observers
```
