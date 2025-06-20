package com.journal.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.*;

public class Subject {
    private Long id;
    private String name;
    private String description;
    private Integer hours;

    // Конструктор по умолчанию для Jackson
    public Subject() {
    }

    public Subject(Long id, String name, String description, Integer hours) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.hours = hours;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    @Override
    public String toString() {
        return "Subject{id=" + id + ", name='" + name + "', description='" + description + "', hours=" + hours + "}";
    }
}
