package com.feedbackflow.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Course {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true) private String courseCode;
    @Column(nullable = false) private String title;
    @ManyToOne(optional = false) @JoinColumn(name = "faculty_id", nullable = false)
    private Faculty faculty;
    @OneToMany(mappedBy = "course") private List<StudentCourse> studentCourses = new ArrayList<>();
    @OneToMany(mappedBy = "course") private List<FeedbackForm> feedbackForms = new ArrayList<>();

    protected Course() {}
    public Course(String courseCode, String title, Faculty faculty) { this.courseCode = courseCode; this.title = title; this.faculty = faculty; }
    public Long getId() { return id; }
    public String getCourseCode() { return courseCode; }
    public String getTitle() { return title; }
    public Faculty getFaculty() { return faculty; }
    public void setFaculty(Faculty faculty) { this.faculty = faculty; }
    public List<StudentCourse> getStudentCourses() { return studentCourses; }
    public List<FeedbackForm> getFeedbackForms() { return feedbackForms; }
}