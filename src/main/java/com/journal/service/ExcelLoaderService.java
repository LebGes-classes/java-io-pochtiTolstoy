package com.journal.service;

import com.journal.model.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.*;

public class ExcelLoaderService {
    private static final String DATA_DIR = "data/excel/";
    private static final String EXCEL_FILE = DATA_DIR + "subjects.xlsx";
    private final Map<Long, Subject> subjects = new HashMap<>();
    private final Map<Long, Group> groups = new HashMap<>();
    private final Map<Long, Student> students = new HashMap<>();
    private final Map<Long, Teacher> teachers = new HashMap<>();
    private final Journal journal = new Journal();

    public Journal loadAllData() throws IOException {
        File file = new File(EXCEL_FILE);
        if (!file.exists()) {
            throw new IOException("Excel файл не найден: " + file.getAbsolutePath());
        }

        try {
            loadSubjects();
            loadGroups();
            loadStudents();
            loadTeachers();
            loadTeacherGroups();
            loadGrades();
            loadSchedule();
            return journal;
        } catch (IOException e) {
            throw new IOException("Ошибка при загрузке данных из Excel: " + e.getMessage(), e);
        }
    }

    private void loadSubjects() throws IOException {
        try (FileInputStream fis = new FileInputStream(DATA_DIR + "subjects.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue; // Skip header
                
                Long id = (long) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                String description = row.getCell(2).getStringCellValue();
                Integer hours = (int) row.getCell(3).getNumericCellValue();

                Subject subject = new Subject(id, name, description, hours);
                subjects.put(id, subject);
            }
        }
        journal.setSubjects(new ArrayList<>(subjects.values()));
    }

    private void loadGroups() throws IOException {
        try (FileInputStream fis = new FileInputStream(DATA_DIR + "groups.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Long id = (long) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                Integer year = (int) row.getCell(2).getNumericCellValue();
                String speciality = row.getCell(3).getStringCellValue();

                Group group = new Group(id, name, year, speciality);
                group.setStudents(new ArrayList<>());
                groups.put(id, group);
            }
        }
        journal.setGroups(new ArrayList<>(groups.values()));
    }

    private void loadStudents() throws IOException {
        try (FileInputStream fis = new FileInputStream(DATA_DIR + "students.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                try {
                    Long id = (long) row.getCell(0).getNumericCellValue();
                    String name = row.getCell(1).getStringCellValue();
                    Long groupId = (long) row.getCell(2).getNumericCellValue();
                    String birthDateStr = row.getCell(3).getStringCellValue();
                    String email = row.getCell(4).getStringCellValue();

                    Group group = groups.get(groupId);
                    if (group == null) continue;

                    Student student = new Student(id, name, birthDateStr, email);
                    student.setGroup(group);
                    students.put(id, student);
                    group.getStudents().add(student);
                } catch (Exception e) {
                    throw new RuntimeException("Error loading student at row " + row.getRowNum(), e);
                }
            }
        }
    }

    private void loadTeachers() throws IOException {
        try (FileInputStream fis = new FileInputStream(DATA_DIR + "teachers.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Long id = (long) row.getCell(0).getNumericCellValue();
                String name = row.getCell(1).getStringCellValue();
                String contacts = row.getCell(2).getStringCellValue();

                Teacher teacher = new Teacher(id, name, contacts);
                teachers.put(id, teacher);
            }
        }
        // Устанавливаем первого учителя как основного
        if (!teachers.isEmpty()) {
            journal.setTeacher(teachers.values().iterator().next());
        }
    }

    private void loadTeacherGroups() throws IOException {
        try (FileInputStream fis = new FileInputStream(DATA_DIR + "teacher_groups.xlsx");
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;

                Long teacherId = (long) row.getCell(0).getNumericCellValue();
                Long groupId = (long) row.getCell(1).getNumericCellValue();

                Teacher teacher = teachers.get(teacherId);
                Group group = groups.get(groupId);

                if (teacher == null || group == null) {
                    System.out.println("Warning: Teacher or group not found for teacher_id: " + teacherId + ", group_id: " + groupId);
                    continue;
                }

                teacher.getGroups().add(group);
            }
        }
    }

    private void loadGrades() throws IOException {
        String gradesFile = DATA_DIR + "grades.xlsx";
        File file = new File(gradesFile);
        if (!file.exists()) {
            System.out.println("Файл с оценками не найден: " + file.getAbsolutePath());
            return;
        }
        try (FileInputStream fis = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // пропускаем заголовок
                Row row = sheet.getRow(i);
                if (row == null) continue;
                long studentId = (long) row.getCell(0).getNumericCellValue();
                long subjectId = (long) row.getCell(1).getNumericCellValue();
                int grade = (int) row.getCell(2).getNumericCellValue();
                // date можно добавить если нужно: String date = row.getCell(3).getStringCellValue();

                Student student = students.get(studentId);
                if (student == null) continue;
                // Добавляем оценку студенту
                student.getGrades().computeIfAbsent(subjectId, k -> new java.util.ArrayList<>()).add(grade);
            }
        }
    }

    private void loadSchedule() throws IOException {
        String scheduleFile = DATA_DIR + "schedule.xlsx";
        File file = new File(scheduleFile);
        if (!file.exists()) {
            System.out.println("Файл с расписанием не найден: " + file.getAbsolutePath());
            return;
        }
        
        try (FileInputStream fis = new FileInputStream(file); 
             Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) { // пропускаем заголовок
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                Long id = (long) row.getCell(0).getNumericCellValue();
                Long groupId = (long) row.getCell(1).getNumericCellValue();
                Long subjectId = (long) row.getCell(2).getNumericCellValue();
                String dayOfWeekStr = row.getCell(3).getStringCellValue();
                String startTimeStr = row.getCell(4).getStringCellValue();
                String endTimeStr = row.getCell(5).getStringCellValue();
                String room = row.getCell(6).getStringCellValue();

                DayOfWeek dayOfWeek = DayOfWeek.valueOf(dayOfWeekStr);
                LocalTime startTime = LocalTime.parse(startTimeStr);
                LocalTime endTime = LocalTime.parse(endTimeStr);

                Schedule schedule = new Schedule(id, groupId, subjectId, dayOfWeek, startTime, endTime, room);
                journal.getSchedule().add(schedule);
            }
        }
    }
}
