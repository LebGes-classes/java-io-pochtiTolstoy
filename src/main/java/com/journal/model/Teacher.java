package com.journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;

public class Teacher {
    private Long id;
    private String name;
    private String contacts;
    
    @JsonIgnore
    private List<Group> groups;

    // Конструктор по умолчанию для Jackson
    public Teacher() {
        this.groups = new ArrayList<>();
    }

    public Teacher(Long id, String name, String contacts) {
        this.id = id;
        this.name = name;
        this.contacts = contacts;
        this.groups = new ArrayList<>();
    }

    public void addGroup(Group group) {
        groups.add(group);
    }

    public double getAverageGrade(Long subjectId) {
        if (groups.isEmpty()) {
            return 0.0;
        }
        return groups.stream()
                .mapToDouble(group -> group.getAverageGrade(subjectId))
                .average()
                .orElse(0.0);
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }
    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { this.groups = groups; }

    @Override
    public String toString() {
        return "Teacher{id=" + id + ", name='" + name + "', contacts='" + contacts + "', groups=" + groups + "}";
    }
}
