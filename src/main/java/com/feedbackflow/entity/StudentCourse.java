package com.feedbackflow.entity;

import jakarta.persistence.*;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "course_id"}))
public class StudentCourse {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "student_id", nullable = false)
    private Student student;
    @ManyToOne(optional = false) @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    protected StudentCourse() {}
    public StudentCourse(Student student, Course course) { this.student = student; this.course = course; }
    public Long getId() { return id; }
    public Student getStudent() { return student; }
    public Course getCourse() { return course; }
}