package com.feedbackflow.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Student {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String rollNumber;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String department;
    // Demo only: real applications must hash passwords and use Spring Security.
    @Column(nullable = false) private String password;
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StudentCourse> studentCourses = new ArrayList<>();
    @OneToMany(mappedBy = "student") private List<FeedbackSubmission> submissions = new ArrayList<>();

    protected Student() {}
    public Student(String name, String rollNumber, String email, String department, String password) {
        this.name = name; this.rollNumber = rollNumber; this.email = email; this.department = department; this.password = password;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getRollNumber() { return rollNumber; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getPassword() { return password; }
    public List<StudentCourse> getStudentCourses() { return studentCourses; }
    public List<FeedbackSubmission> getSubmissions() { return submissions; }
}