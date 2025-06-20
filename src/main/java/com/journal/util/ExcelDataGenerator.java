package com.journal.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class ExcelDataGenerator {
    private static final String DATA_DIR = "data/excel/";

    public static void main(String[] args) {
        try {
            generateSubjects();
            generateGroups();
            generateStudents();
            generateTeacher();
            generateTeacherGroups();
            generateGrades();
            generateSchedule();
        } catch (IOException e) {
            throw new RuntimeException("Error generating test data", e);
        }
    }

    private static void generateSubjects() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Subjects");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("description");
            headerRow.createCell(3).setCellValue("hours");

            // Data
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, "Mathematics", "Basic math course", 120),
                Arrays.asList(2L, "Physics", "Classical mechanics", 90),
                Arrays.asList(3L, "Computer Science", "Programming basics", 150),
                Arrays.asList(4L, "English", "Language course", 60),
                Arrays.asList(5L, "History", "World history", 75)
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "subjects.xlsx");
        }
    }

    private static void generateGroups() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Groups");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("year");
            headerRow.createCell(3).setCellValue("specialty");

            // Data
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, "CS-101", 2023, "Computer Science"),
                Arrays.asList(2L, "PH-101", 2023, "Physics"),
                Arrays.asList(3L, "CS-102", 2022, "Computer Science"),
                Arrays.asList(4L, "MA-101", 2023, "Mathematics"),
                Arrays.asList(5L, "EN-101", 2023, "English")
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "groups.xlsx");
        }
    }

    private static void generateStudents() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Students");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("group_id");
            headerRow.createCell(3).setCellValue("birth_date");
            headerRow.createCell(4).setCellValue("contacts");

            // Data
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, "John Doe", 1L, "2000-01-15", "john@email.com"),
                Arrays.asList(2L, "Jane Smith", 1L, "2000-03-20", "jane@email.com"),
                Arrays.asList(3L, "Bob Johnson", 2L, "2000-05-10", "bob@email.com"),
                Arrays.asList(4L, "Alice Brown", 3L, "2001-07-25", "alice@email.com"),
                Arrays.asList(5L, "Charlie Wilson", 4L, "2000-09-30", "charlie@email.com")
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "students.xlsx");
        }
    }

    private static void generateTeacher() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Teachers");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("contacts");

            // Data - только один учитель
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, "Prof. Smith", "smith@university.edu")
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "teachers.xlsx");
        }
    }

    private static void generateTeacherGroups() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("TeacherGroups");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("teacher_id");
            headerRow.createCell(1).setCellValue("group_id");

            // Data - один учитель ведет все группы
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, 1L),
                Arrays.asList(1L, 2L),
                Arrays.asList(1L, 3L),
                Arrays.asList(1L, 4L),
                Arrays.asList(1L, 5L)
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "teacher_groups.xlsx");
        }
    }

    private static void generateGrades() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Grades");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("student_id");
            headerRow.createCell(1).setCellValue("subject_id");
            headerRow.createCell(2).setCellValue("grade");
            headerRow.createCell(3).setCellValue("date");

            // Data
            List<List<Object>> data = Arrays.asList(
                Arrays.asList(1L, 1L, 5, "2024-01-15"),
                Arrays.asList(1L, 2L, 4, "2024-01-20"),
                Arrays.asList(2L, 1L, 5, "2024-01-15"),
                Arrays.asList(2L, 3L, 5, "2024-01-25"),
                Arrays.asList(3L, 2L, 4, "2024-01-20"),
                Arrays.asList(5L, 4L, 4, "2024-01-30")
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "grades.xlsx");
        }
    }

    private static void generateSchedule() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Schedule");
            
            // Headers
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("group_id");
            headerRow.createCell(2).setCellValue("subject_id");
            headerRow.createCell(3).setCellValue("day_of_week");
            headerRow.createCell(4).setCellValue("start_time");
            headerRow.createCell(5).setCellValue("end_time");
            headerRow.createCell(6).setCellValue("room");

            // Data - расписание для первых трех групп
            List<List<Object>> data = Arrays.asList(
                // Группа 1
                Arrays.asList(1L, 1L, 1L, "MONDAY", "09:00", "10:30", "101"),
                Arrays.asList(2L, 1L, 2L, "MONDAY", "10:45", "12:15", "102"),
                Arrays.asList(3L, 1L, 3L, "TUESDAY", "09:00", "10:30", "103"),
                Arrays.asList(4L, 1L, 4L, "WEDNESDAY", "09:00", "10:30", "104"),
                Arrays.asList(5L, 1L, 5L, "THURSDAY", "09:00", "10:30", "105"),
                
                // Группа 2
                Arrays.asList(6L, 2L, 1L, "MONDAY", "13:00", "14:30", "201"),
                Arrays.asList(7L, 2L, 2L, "TUESDAY", "13:00", "14:30", "202"),
                Arrays.asList(8L, 2L, 3L, "WEDNESDAY", "13:00", "14:30", "203"),
                Arrays.asList(9L, 2L, 4L, "THURSDAY", "13:00", "14:30", "204"),
                Arrays.asList(10L, 2L, 5L, "FRIDAY", "13:00", "14:30", "205"),
                
                // Группа 3
                Arrays.asList(11L, 3L, 1L, "MONDAY", "15:00", "16:30", "301"),
                Arrays.asList(12L, 3L, 2L, "TUESDAY", "15:00", "16:30", "302"),
                Arrays.asList(13L, 3L, 3L, "WEDNESDAY", "15:00", "16:30", "303"),
                Arrays.asList(14L, 3L, 4L, "THURSDAY", "15:00", "16:30", "304"),
                Arrays.asList(15L, 3L, 5L, "FRIDAY", "15:00", "16:30", "305")
            );

            fillSheet(sheet, data);
            saveWorkbook(workbook, "schedule.xlsx");
        }
    }

    private static void fillSheet(Sheet sheet, List<List<Object>> data) {
        int rowNum = 1;
        for (List<Object> rowData : data) {
            Row row = sheet.createRow(rowNum++);
            int colNum = 0;
            for (Object value : rowData) {
                Cell cell = row.createCell(colNum++);
                if (value instanceof Long) {
                    cell.setCellValue((Long) value);
                } else if (value instanceof String) {
                    cell.setCellValue((String) value);
                } else if (value instanceof Integer) {
                    cell.setCellValue((Integer) value);
                } else if (value instanceof LocalDate) {
                    cell.setCellValue(((LocalDate) value).toEpochDay());
                    CellStyle dateStyle = sheet.getWorkbook().createCellStyle();
                    dateStyle.setDataFormat(sheet.getWorkbook().createDataFormat().getFormat("yyyy-mm-dd"));
                    cell.setCellStyle(dateStyle);
                }
            }
        }
    }

    private static void saveWorkbook(Workbook workbook, String fileName) throws IOException {
        try (FileOutputStream fileOut = new FileOutputStream(DATA_DIR + fileName)) {
            workbook.write(fileOut);
        }
    }
}
