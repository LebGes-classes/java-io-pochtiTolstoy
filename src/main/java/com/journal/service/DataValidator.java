package com.journal.service;

import com.journal.model.*;
import java.util.*;

public class DataValidator {
    private final List<String> errors = new ArrayList<>();

    public List<String> validate(Journal journal) {
        validateSubjects(journal.getSubjects());
        validateGroups(journal.getGroups());
        validateStudents(journal.getAllStudents());
        validateTeacher(journal.getTeacher(), journal.getGroups());
        return errors;
    }

    private void validateSubjects(List<Subject> subjects) {
        Set<Long> ids = new HashSet<>();
        for (Subject subject : subjects) {
            if (!ids.add(subject.getId())) {
                errors.add("Дублирующийся ID предмета: " + subject.getId());
            }
        }
    }

    private void validateGroups(List<Group> groups) {
        Set<Long> ids = new HashSet<>();
        for (Group group : groups) {
            if (!ids.add(group.getId())) {
                errors.add("Дублирующийся ID группы: " + group.getId());
            }
        }
    }

    private void validateStudents(List<Student> students) {
        Set<Long> ids = new HashSet<>();
        for (Student student : students) {
            if (!ids.add(student.getId())) {
                errors.add("Дублирующийся ID студента: " + student.getId());
            }
            if (student.getGroup() == null) {
                errors.add("Студент " + student.getId() + " не привязан к группе");
            }
        }
    }

    private void validateTeacher(Teacher teacher, List<Group> allGroups) {
        if (teacher == null) {
            errors.add("Учитель не назначен");
            return;
        }

        Set<Long> groupIds = new HashSet<>();
        Set<Long> allGroupIds = allGroups.stream()
                .map(Group::getId)
                .collect(HashSet::new, HashSet::add, HashSet::addAll);

        for (Group group : teacher.getGroups()) {
            if (!groupIds.add(group.getId())) {
                errors.add("Дублирующаяся группа у учителя: " + group.getId());
            }
            if (!allGroupIds.contains(group.getId())) {
                errors.add("Учитель назначен на несуществующую группу: " + group.getId());
            }
        }
    }
}
