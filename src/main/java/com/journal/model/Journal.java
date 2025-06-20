package com.journal.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class Journal {
    private Teacher teacher;
    private List<Group> groups;
    private List<Subject> subjects;
    private List<Schedule> schedule;
    private boolean modified;

    public Journal() {
        this.groups = new ArrayList<>();
        this.subjects = new ArrayList<>();
        this.schedule = new ArrayList<>();
        this.modified = false;
    }

    // Геттеры и сеттеры
    public Teacher getTeacher() { return teacher; }
    public void setTeacher(Teacher teacher) { 
        this.teacher = teacher;
        this.modified = true;
    }
    public List<Group> getGroups() { return groups; }
    public void setGroups(List<Group> groups) { 
        this.groups = groups;
        this.modified = true;
    }
    public List<Subject> getSubjects() { return subjects; }
    public void setSubjects(List<Subject> subjects) { 
        this.subjects = subjects;
        this.modified = true;
    }
    public boolean isModified() { return modified; }
    public void setModified(boolean modified) { this.modified = modified; }
    public List<Schedule> getSchedule() {
        return schedule;
    }
    public void setSchedule(List<Schedule> schedule) {
        this.schedule = schedule;
    }

    // Вспомогательные методы
    @JsonIgnore
    public List<Student> getAllStudents() {
        return groups.stream()
                .flatMap(group -> group.getStudents().stream())
                .toList();
    }

    public void restoreRelations() {
        if (teacher != null) {
            // Восстанавливаем связи учителя с группами
            for (Group group : groups) {
                teacher.addGroup(group);
                group.setTeacher(teacher);
            }
        }

        // Восстанавливаем связи групп со студентами
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                student.setGroup(group);
            }
        }
    }

    @Override
    public String toString() {
        return "Journal{" +
                "teacher=" + teacher +
                ", groups=" + groups +
                ", subjects=" + subjects +
                ", modified=" + modified +
                '}';
    }

    public void addGrade(Long studentId, Long subjectId, int grade) {
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                if (student.getId().equals(studentId)) {
                    student.addGrade(subjectId, grade);
                    modified = true;
                    return;
                }
            }
        }
    }

    public double getStudentAverageGrade(Long studentId, Long subjectId) {
        for (Group group : groups) {
            for (Student student : group.getStudents()) {
                if (student.getId().equals(studentId)) {
                    return student.getAverageGrade(subjectId);
                }
            }
        }
        return 0.0;
    }

    public double getGroupAverageGrade(Long groupId, Long subjectId) {
        for (Group group : groups) {
            if (group.getId().equals(groupId)) {
                return group.getAverageGrade(subjectId);
            }
        }
        return 0.0;
    }

    public void addSchedule(Schedule schedule) {
        this.schedule.add(schedule);
        modified = true;
    }

    public void removeSchedule(Long scheduleId) {
        this.schedule.removeIf(s -> s.getId().equals(scheduleId));
        modified = true;
    }

    public List<Schedule> getGroupSchedule(Long groupId) {
        return schedule.stream()
            .filter(s -> s.getGroupId().equals(groupId))
            .toList();
    }

    public List<Schedule> getSubjectSchedule(Long subjectId) {
        return schedule.stream()
            .filter(s -> s.getSubjectId().equals(subjectId))
            .toList();
    }
}
