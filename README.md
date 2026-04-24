## 📌 About the Project

**ALAMS** (Adaptive Learning and Assessment Management System) is a full-stack web application that bridges the gap between professors and students in a digital learning environment.

Unlike traditional LMS platforms that deliver the same content to every student, ALAMS features an **adaptive quiz engine** — quiz difficulty (Easy / Medium / Hard) automatically adjusts based on each student's past performance. Professors can upload materials, create multi-difficulty quiz questions, and view course-wide analytics. Students get a personalised learning experience with a real-time progress dashboard.

### ✨ Key Features

- 🔐 **Role-based authentication** — separate dashboards for Professors and Students
- 📚 **Course & material management** — upload PDF, PPT, DOCX files
- 🧠 **Adaptive quiz engine** — difficulty auto-adjusts per student performance
- 📊 **Analytics dashboard** — Strategy Pattern allows runtime switching between student and course reports
- 🔔 **Event-driven architecture** — Observer Pattern notifies and logs on every quiz submission
- 🏗️ **Clean MVC architecture** — enforced by Spring Boot

---

## 👥 Team

| Name | SRN | Module | Pattern | Principle |
|------|-----|--------|---------|-----------|
| Harsha Madev Hegde | PES2UG23CS212 | User Auth & Management | Singleton | SRP |
| H Achyuth | PES2UG23CS208 | Course & Material Management | Factory Method | OCP |
| Gowni Ananya | PES2UG23CS204 | Adaptive Quiz Engine | Observer | SRP |
| Harshavardhan N | PES2UG23CS214 | Analytics Dashboard | Strategy | LSP |
| Gudihalli Kiran | PES2UG23CS207 | Adaptive Difficulty Adjuster | Observer + Singleton | DIP |

**Guide:** Prof. Sowmya A M

---

## 🛠️ Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.2 |
| Architecture | Spring MVC |
| View | Thymeleaf + CSS |
| Security | Spring Security (BCrypt) |
| ORM | Spring Data JPA + Hibernate |
| Database | MySQL 8.0 |
| Build Tool | Maven 3.8 |

---

## 🏗️ MVC Architecture

```
Browser  →  Controller  →  Service  →  Repository  →  MySQL
                ↓
            Thymeleaf View
```

| Layer | Technology | Files |
|-------|-----------|-------|
| **Model** | JPA Entities | `User`, `Course`, `Material`, `Quiz`, `QuizSubmission` |
| **View** | Thymeleaf HTML | `login.html`, `dashboard.html`, `take-quiz.html`, `analytics.html` |
| **Controller** | Spring `@Controller` | `AuthController`, `CourseController`, `QuizController`, `AnalyticsController` |
| **Service** | Spring `@Service` | `UserService`, `CourseService`, `QuizService`, `AnalyticsContext` |
| **Repository** | Spring Data JPA | `UserRepository`, `CourseRepository`, `QuizRepository` |

---

## 🎨 Design Patterns

### 1. Singleton Pattern — `UserService.java`
> **Owner: Harsha Madev Hegde**

Spring `@Service` creates exactly **one instance** of `UserService` for the entire application. It is shared across `AuthController`, `AnalyticsController`, and `SecurityConfig` without ever creating multiple copies.

```java
@Service  // one instance created, shared everywhere
public class UserService {
    public User register(...) { ... }
    public Optional<User> findByUsername(...) { ... }
}
```

---

### 2. Factory Method Pattern — `MaterialFactory.java`
> **Owner: H Achyuth**

An abstract `MaterialFactory` class defines the `createMaterial()` factory method. Concrete subclasses (`PdfMaterialFactory`, `PresentationMaterialFactory`, `DocumentMaterialFactory`, `GenericMaterialFactory`) each decide what type of `Material` to create based on the uploaded file extension.

```java
// Factory selected automatically based on file type
MaterialFactory factory = MaterialFactory.getFactory(file);
Material material = factory.createMaterial(file, course, storedPath);
```

---

### 3. Observer Pattern — `QuizSubmittedEvent.java`
> **Owner: Gowni Ananya & Gudihalli Kiran**

After every quiz submission, `QuizService` publishes a `QuizSubmittedEvent`. Three independent listeners react automatically:

- `ScoreAuditListener` → logs the event for audit trail
- `ProfessorNotificationListener` → notifies the professor
- `AdaptiveDifficultyListener` → computes the next difficulty level

```java
// Publisher (QuizService)
eventPublisher.publishEvent(new QuizSubmittedEvent(...));

// Observers react automatically (no change to QuizService needed)
@EventListener
public void onQuizSubmitted(QuizSubmittedEvent event) { ... }
```

---

### 4. Strategy Pattern — `AnalyticsStrategy.java`
> **Owner: Harshavardhan N**

`AnalyticsContext` holds a reference to an `AnalyticsStrategy` interface. The algorithm is swapped at runtime — `StudentPerformanceStrategy` for student reports, `CoursePerformanceStrategy` for professor reports — without changing any controller logic.

```java
// Switch algorithm at runtime
analyticsContext.setStrategy(studentStrategy);   // student view
analyticsContext.setStrategy(courseStrategy);    // professor view
List<Map<String,Object>> report = analyticsContext.executeReport(param);
```

---

## 📐 Design Principles

| Principle | Full Name | Applied In |
|-----------|-----------|-----------|
| **SRP** | Single Responsibility | Each class has one job — `UserService` handles only user logic, `AuthController` handles only HTTP routing |
| **OCP** | Open/Closed | `MaterialFactory` — add new file types by adding a new subclass, never modify existing code |
| **LSP** | Liskov Substitution | `StudentPerformanceStrategy` and `CoursePerformanceStrategy` are fully substitutable in `AnalyticsContext` |
| **DIP** | Dependency Inversion | Controllers depend on service abstractions via `@Autowired`, never on concrete classes directly |

---

## 📁 Project Structure

```
ALAMS/
├── pom.xml                                         ← Maven dependencies
├── src/
│   ├── main/
│   │   ├── java/com/alams/
│   │   │   ├── AlamApplication.java                ← Main entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java             ← Spring Security config
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java             ← Login / Register / Dashboard
│   │   │   │   ├── CourseController.java           ← Course + Material upload
│   │   │   │   ├── QuizController.java             ← Quiz CRUD + submission
│   │   │   │   ├── AnalyticsController.java        ← Analytics (Strategy pattern)
│   │   │   │   └── StudentController.java          ← Student results page
│   │   │   ├── model/
│   │   │   │   ├── User.java                       ← User entity
│   │   │   │   ├── Course.java                     ← Course entity
│   │   │   │   ├── Material.java                   ← Material entity
│   │   │   │   ├── Quiz.java                       ← Quiz entity
│   │   │   │   └── QuizSubmission.java             ← Submission entity
│   │   │   ├── repository/
│   │   │   │   ├── UserRepository.java             ← User DB queries
│   │   │   │   └── Repositories.java               ← Course, Material, Quiz, Submission repos
│   │   │   ├── service/
│   │   │   │   ├── UserService.java                ← Singleton pattern ★
│   │   │   │   ├── CourseService.java              ← Uses Factory Method
│   │   │   │   ├── QuizService.java                ← Publishes Observer events
│   │   │   │   ├── material/
│   │   │   │   │   ├── MaterialFactory.java        ← Factory Method abstract class ★
│   │   │   │   │   └── ConcreteFactories.java      ← Pdf / Presentation / Document / Generic
│   │   │   │   └── analytics/
│   │   │   │       ├── AnalyticsStrategy.java      ← Strategy interface ★
│   │   │   │       ├── StudentPerformanceStrategy.java
│   │   │   │       ├── CoursePerformanceStrategy.java
│   │   │   │       └── AnalyticsContext.java       ← Strategy context ★
│   │   │   └── events/
│   │   │       ├── QuizSubmittedEvent.java         ← Observer event ★
│   │   │       └── QuizEventListeners.java         ← 3 concrete observers ★
│   │   └── resources/
│   │       ├── application.properties              ← DB config, server port
│   │       ├── schema.sql                          ← MySQL schema + sample data
│   │       ├── static/css/style.css                ← Global stylesheet
│   │       └── templates/
│   │           ├── login.html
│   │           ├── register.html
│   │           ├── professor/
│   │           │   ├── dashboard.html
│   │           │   ├── course-detail.html
│   │           │   ├── create-course.html
│   │           │   └── analytics.html
│   │           └── student/
│   │               ├── dashboard.html
│   │               ├── course-detail.html
│   │               ├── take-quiz.html
│   │               ├── results.html
│   │               └── analytics.html
```

---

## 🚀 Getting Started

### Prerequisites

Make sure you have these installed:

- [Java 17+](https://adoptium.net/)
- [Maven 3.8+](https://maven.apache.org/download.cgi)
- [MySQL 8.0+](https://dev.mysql.com/downloads/)

Check versions:
```bash
java -version
mvn -version
mysql --version
```

---

### Step 1 — Clone the repository

```bash
git clone https://github.com/PES2UG23CS212/alams-ooad-project.git
cd alams-ooad-project
```

---

### Step 2 — Set up the database

```bash
mysql -u root -p < src/main/resources/schema.sql
```

This creates the `alams_db` database with all tables and sample data.

---

### Step 3 — Configure database credentials

Open `src/main/resources/application.properties` and update:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/alams_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

---

### Step 4 — Run the project

```bash
mvn spring-boot:run
```

---

### Step 5 — Open in browser

```
http://localhost:8080
```

---

## 🔑 Default Login Credentials

| Username | Password | Role |
|----------|----------|------|
| `prof.sowmya` | `password123` | Professor |
| `harsha.madev` | `password123` | Student |
| `h.achyuth` | `password123` | Student |
| `gowni.ananya` | `password123` | Student |

> Passwords are BCrypt-hashed in the database. The plain text `password123` is only for initial testing.

---

## 🖥️ Application Flow

```
/login  →  Spring Security authenticates  →  /dashboard
                                               ↓
                              isProfessor?  ──────────────────────────
                                   ↓ YES                              ↓ NO
                        professor/dashboard                  student/dashboard
                               ↓                                    ↓
                     Create Course                         Browse Courses
                     Upload Material  ←─ Factory Method    View Materials
                     Add Quiz Questions                    Take Adaptive Quiz ← Observer
                     View Analytics   ← Strategy                    ↓
                                                           View My Progress ← Strategy
```

---

## ⚙️ Alternative Run Commands

```bash
# Build JAR and run
mvn package
java -jar target/adaptive-learning-assessment-1.0.0.jar

# Run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Skip tests and run
mvn spring-boot:run -DskipTests
```

---

## 📄 License

This project is submitted as an academic mini-project for **UE23CS352B – Object Oriented Analysis & Design** at PES University, Bengaluru.

---

<div align="center">
Made with ☕ and Spring Boot by Team ALAMS — PES University 2026
</div>
