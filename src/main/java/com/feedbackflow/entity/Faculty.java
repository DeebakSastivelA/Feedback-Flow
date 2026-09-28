package com.feedbackflow.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Faculty {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private String name;
    @Column(nullable = false, unique = true) private String email;
    @Column(nullable = false) private String department;
    // Demo only: real applications must hash passwords and use Spring Security.
    @Column(nullable = false) private String password;
    @OneToMany(mappedBy = "faculty") private List<Course> courses = new ArrayList<>();

    protected Faculty() {}
    public Faculty(String name, String email, String department, String password) {
        this.name = name; this.email = email; this.department = department; this.password = password;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getPassword() { return password; }
    public List<Course> getCourses() { return courses; }
}