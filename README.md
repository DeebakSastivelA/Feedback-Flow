# Feedbackflow

Feedbackflow is a course feedback collection system for a college department. Administrators manage people, course assignments, and semester feedback forms; students submit one rating set per assigned course form; faculty review question-level and overall course averages.

## Technology

- Java 25 and Maven
- Spring Boot 4.1.1
- Spring Web MVC, Spring Data JPA, Bean Validation, and the MySQL Driver
- MySQL
- Plain HTML, CSS, and vanilla JavaScript using the Fetch API

No other runtime libraries, frontend frameworks, CDN assets, or external fonts are used. Passwords are plain text for this demonstration only. The entity source comments explain that real applications must hash passwords and use Spring Security.

## Project Structure

```text
feedbackflow/
├── README.md
├── mvnw
├── mvnw.cmd
├── pom.xml
└── src/
    └── main/
        ├── java/com/feedbackflow/
        │   ├── FeedbackFlowApplication.java
        │   ├── config/
        │   │   └── package-info.java
        │   ├── controller/
        │   │   ├── AdminController.java
        │   │   ├── AuthController.java
        │   │   ├── CourseController.java
        │   │   ├── FeedbackFormController.java
        │   │   ├── FeedbackController.java
        │   │   └── FacultyController.java
        │   ├── dto/
        │   │   ├── LoginRequest.java
        │   │   ├── CreateStudentRequest.java
        │   │   ├── CreateFacultyRequest.java
        │   │   ├── CreateCourseRequest.java
        │   │   ├── CreateFeedbackFormRequest.java
        │   │   ├── SubmitFeedbackRequest.java
        │   │   ├── AnswerRequest.java
        │   │   ├── DashboardResponse.java
        │   │   ├── FeedbackSummaryResponse.java
        │   │   └── ApiResponses.java
        │   ├── entity/
        │   │   ├── Faculty.java
        │   │   ├── Student.java
        │   │   ├── Course.java
        │   │   ├── StudentCourse.java
        │   │   ├── FeedbackForm.java
        │   │   ├── Question.java
        │   │   ├── FeedbackSubmission.java
        │   │   ├── Response.java
        │   │   └── FormStatus.java
        │   ├── exception/
        │   │   ├── ResourceNotFoundException.java
        │   │   ├── DuplicateFeedbackException.java
        │   │   ├── FormClosedException.java
        │   │   ├── InvalidCredentialsException.java
        │   │   └── GlobalExceptionHandler.java
        │   ├── repository/
        │   │   ├── FacultyRepository.java
        │   │   ├── StudentRepository.java
        │   │   ├── CourseRepository.java
        │   │   ├── StudentCourseRepository.java
        │   │   ├── FeedbackFormRepository.java
        │   │   ├── QuestionRepository.java
        │   │   ├── FeedbackSubmissionRepository.java
        │   │   └── ResponseRepository.java
        │   └── service/
        │       ├── AdminService.java
        │       ├── AuthService.java
        │       ├── CourseService.java
        │       ├── FeedbackFormService.java
        │       ├── FeedbackService.java
        │       ├── FacultyService.java
        │       └── ApiMapper.java
        └── resources/
            ├── application.properties
            └── static/
                ├── admin.html
                ├── index.html
                ├── style.css
                ├── app.js
                └── admin.js
```

## Database Setup

Install and start MySQL, and install a Java 25 JDK. The default configuration in `src/main/resources/application.properties` is:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/feedbackflow_db?createDatabaseIfNotExist=true
spring.datasource.username=root
spring.datasource.password=dbak
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
server.port=8080
```

The URL creates `feedbackflow_db` on connection when the MySQL user has permission. Change the username and password to match your local MySQL installation. Hibernate creates or updates the tables on startup.

## Run

From the project root on Windows:

```powershell
.\mvnw.cmd clean package
.\mvnw.cmd spring-boot:run
```

The Maven wrapper downloads the configured Maven distribution if needed. Open the administrator dashboard and user portal at:

- http://localhost:8080/admin.html
- http://localhost:8080/index.html

Create a faculty member, create a course assigned to that faculty member, and then add students assigned to a course. Publish a form for the course to make it available to those students. The demo has no administrator sign-in or authorization layer; do not expose it to an untrusted network.

## Feedback Rules

- Every published form receives the same six required questions in a fixed order. Administrators cannot edit those questions.
- Every question uses the five-point scale: 5 Strongly Agree, 4 Agree, 3 Somewhat Agree, 2 Disagree, 1 Strongly Disagree.
- A student can submit once for each form, and only for a course assigned to that student. The database has a unique constraint on `(student_id, feedback_form_id)` in addition to service validation.
- All six questions require one rating from 1 to 5. Answers referring to another form's questions are rejected.
- A form cannot accept submissions after its closing date or after an administrator closes it. The closing date remains open through that calendar date.
- Faculty averages aggregate submitted ratings from feedback forms for each assigned course. The summary reports submission counts, per-question averages, overall average, and lowest-rated question.
- Students with feedback submissions and faculty with assigned courses cannot be deleted. These operations return `409 Conflict` with a clear message.

## API

All request and response bodies are JSON DTOs. Validation and business-rule failures return a JSON `message`; validation responses also include field errors.

| Method | Endpoint | Purpose |
| --- | --- | --- |
| `POST` | `/api/admin/students` | Create a student and assign one course (`201`) |
| `GET` | `/api/admin/students` | List students |
| `DELETE` | `/api/admin/students/{id}` | Delete a student when allowed |
| `POST` | `/api/admin/faculties` | Create a faculty member and optionally assign existing courses (`201`) |
| `GET` | `/api/admin/faculties` | List faculty members |
| `DELETE` | `/api/admin/faculties/{id}` | Delete faculty when no courses are assigned |
| `POST` | `/api/admin/courses` | Create a course assigned to a faculty member (`201`) |
| `GET` | `/api/admin/courses` | List courses and faculty assignments |
| `POST` | `/api/admin/forms` | Publish a form and create its six questions (`201`) |
| `GET` | `/api/admin/forms` | List forms and submission counts |
| `GET` | `/api/admin/dashboard` | Get student, faculty, course, form, and submission totals |
| `POST` | `/api/auth/login` | Sign in as `STUDENT` or `FACULTY` |
| `GET` | `/api/student/{studentId}/forms` | List assigned course forms and their status |
| `GET` | `/api/student/{studentId}/submissions` | List prior submissions and selected ratings |
| `POST` | `/api/forms/{formId}/submissions` | Submit all six ratings (`201`) |
| `PUT` | `/api/forms/{formId}/close` | Close a form early |
| `GET` | `/api/faculty/{facultyId}/feedback-summary` | Get course and question rating averages |

Common status codes are `200 OK`, `201 Created`, `400 Bad Request`, `404 Not Found`, and `409 Conflict`.