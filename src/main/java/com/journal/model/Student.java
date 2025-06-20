package com.journal.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.*;

public class Student {
    private Long id;
    private String name;
    private String birthDate;
    private String email;
    private Map<Long, List<Integer>> grades; // subjectId -> grades
    
    @JsonBackReference
    private Group group;

    // Конструктор по умолчанию для Jackson
    public Student() {
        this.grades = new HashMap<>();
    }

    public Student(Long id, String name, String birthDate, String email) {
        this.id = id;
        this.name = name;
        this.birthDate = birthDate;
        this.email = email;
        this.grades = new HashMap<>();
    }

    public void addGrade(Long subjectId, int grade) {
        grades.computeIfAbsent(subjectId, k -> new ArrayList<>()).add(grade);
    }

    public double getAverageGrade(Long subjectId) {
        List<Integer> subjectGrades = grades.get(subjectId);
        if (subjectGrades == null || subjectGrades.isEmpty()) {
            return 0.0;
        }
        return subjectGrades.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }
    public Map<Long, List<Integer>> getGrades() { return grades; }
    public void setGrades(Map<Long, List<Integer>> grades) { this.grades = grades; }
    public Group getGroup() { return group; }
    public void setGroup(Group group) { this.group = group; }

    @Override
    public String toString() {
        String groupName = (group != null && group.getName() != null) ? group.getName() : "null";
        return "Student{id=" + id + ", name='" + name + "', birthDate='" + birthDate + "', email='" + email + "', group='" + groupName + "', grades=" + grades + "}";
    }

    public String toPrettyString(Map<Long, Subject> subjectMap) {
        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(id)
          .append(", Имя: ").append(name)
          .append(", Email: ").append(email)
          .append(", Группа: ").append(group != null ? group.getName() : "-")
          .append("\nОценки:");
        if (grades == null || grades.isEmpty()) {
            sb.append(" нет оценок");
        } else {
            for (var entry : grades.entrySet()) {
                Long subjectId = entry.getKey();
                String subjectName = subjectMap.containsKey(subjectId) ? subjectMap.get(subjectId).getName() : ("ID=" + subjectId);
                sb.append("\n  ").append(subjectName).append(": ").append(entry.getValue());
                // Средний балл
                var values = entry.getValue();
                if (!values.isEmpty()) {
                    double avg = values.stream().mapToInt(Integer::intValue).average().orElse(0);
                    sb.append("    Средний балл: ").append(String.format("%.2f", avg));
                }
            }
        }
        return sb.toString();
    }
}
