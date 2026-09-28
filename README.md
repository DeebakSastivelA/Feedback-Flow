FeedbackFlow - Course Feedback Collection System

FeedbackFlow is a Java Spring Boot web application for collecting and analyzing end-of-semester course feedback. It replaces paper-based feedback forms with a simple digital system where administrators manage academic data, students submit ratings, and faculty view course feedback summaries.

Features

Admin dashboard showing total students, faculty, courses, forms, and completed feedback submissions

Add and delete students and faculty members

Create courses and assign faculty members

Assign students to courses

Create feedback forms with six fixed course-evaluation questions

Student and faculty login portal

One feedback submission per student for each form

Automatic deadline and closed-form validation

Faculty-wise and question-wise feedback averages

Course-level overall average rating

Department-specific student roll-number validation

Academic email validation:

Students: name@stu.edu.in

Faculty: name@prof.edu.in

Clear validation and error messages

Technologies Used

Java 25

Spring Boot

Spring Web

Spring Data JPA

Spring Validation

MySQL

HTML

CSS

Vanilla JavaScript

Rating Scale

Rating

Meaning

5

Strongly Agree

4

Agree

3

Somewhat Agree

2

Disagree

1

Strongly Disagree

Run Locally

Create a MySQL database named feedbackflow_db.

Update MySQL username and password in src/main/resources/application.properties.

Run the application:

.\mvnw.cmd spring-boot:run

Open these pages:

Admin Dashboard: http://localhost:8080/admin.html
Student and Faculty Portal: http://localhost:8080/index.html
