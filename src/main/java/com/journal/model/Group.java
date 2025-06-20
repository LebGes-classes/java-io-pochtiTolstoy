package com.journal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.List;

public class Group {
    private Long id;
    private String name;
    private Integer year;
    private String specialty;
    @JsonManagedReference
    private List<Student> students;

    @JsonBackReference
    private Teacher teacher;

    // Конструктор по умолчанию для Jackson
    public Group() {
        this.students = new ArrayList<>();
    }

    public Group(Long id, String name, Integer year, String speciality) {
        this.id = id;
        this.name = name;
        this.year = year;
        this.specialty = speciality;
        this.students = new ArrayList<>();
    }

    public void addStudent(Student student) {
        students.add(student);
        student.setGroup(this);
    }

    public double getAverageGrade(Long subjectId) {
        if (students.isEmpty()) {
            return 0.0;
        }
        return students.stream()
                .mapToDouble(student -> student.getAverageGrade(subjectId))
                .average()
                .orElse(0.0);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    public String getSpecialty() { return specialty; }
    public void setSpecialty(String specialty) { this.specialty = specialty; }
    public List<Student> getStudents() { return students; }
    public void setStudents(List<Student> students) { this.students = students; }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }

    @Override
    public String toString() {
        return "Group{id=" + id + ", name='" + name + "', year=" + year + 
               ", specialty='" + specialty + "', students=" + students + "}";
    }
}
