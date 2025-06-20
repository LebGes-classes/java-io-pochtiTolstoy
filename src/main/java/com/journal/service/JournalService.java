package com.journal.service;

import com.journal.model.Journal;
import com.journal.model.Student;
import com.journal.model.Subject;
import com.journal.model.Group;
import com.journal.model.Teacher;
import com.journal.model.Schedule;
import java.util.List;
import java.io.IOException;
import java.io.File;
import java.io.FileOutputStream;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.util.Map;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.DayOfWeek;

public class JournalService {
    private final JsonSerializer jsonSerializer;
    private final ExcelLoaderService excelLoaderService;
    private Journal journal;

    public JournalService() throws IOException {
        this.jsonSerializer = new JsonSerializer();
        this.excelLoaderService = new ExcelLoaderService();
        this.journal = loadJournal();
    }

    public Journal loadJournal() throws IOException {
        System.out.println("Начинаю загрузку журнала...");
        
        // Сначала проверяем JSON
        System.out.println("Проверяю наличие JSON файла...");
        Journal journal = jsonSerializer.deserialize();
        if (journal != null) {
            System.out.println("Загружен журнал из JSON");
            return journal;
        }

        // Если JSON не существует или пустой, загружаем из Excel
        System.out.println("JSON не найден или пустой, загружаю из Excel...");
        try {
            journal = excelLoaderService.loadAllData();
            System.out.println("Данные загружены из Excel, сохраняю в JSON...");
            jsonSerializer.serialize(journal);
            System.out.println("Загружен журнал из Excel");
            return journal;
        } catch (IOException e) {
            System.err.println("\nОШИБКА: Не удалось загрузить данные!");
            System.err.println("1. Проверьте наличие файла journal.json в папке data/");
            System.err.println("2. Если JSON файла нет, проверьте наличие Excel файлов в папке data/excel/");
            System.err.println("   - grades.xlsx");
            System.err.println("   - groups.xlsx");
            System.err.println("   - students.xlsx");
            System.err.println("   - subjects.xlsx");
            System.err.println("   - teacher_groups.xlsx");
            System.err.println("   - teachers.xlsx");
            System.err.println("\nПодробности ошибки: " + e.getMessage());
            throw e;
        }
    }

    public void saveJournal() throws IOException {
        jsonSerializer.serialize(journal);
    }

    public List<Student> getStudents() {
        return journal.getAllStudents();
    }

    public List<Subject> getSubjects() {
        return journal.getSubjects();
    }

    public List<Group> getGroups() {
        return journal.getGroups();
    }

    public void addGrade(Long studentId, Long subjectId, int grade) throws IOException {
        journal.addGrade(studentId, subjectId, grade);
        saveJournal();
    }

    public double getStudentAverageGrade(Long studentId, Long subjectId) {
        return journal.getStudentAverageGrade(studentId, subjectId);
    }

    public double getGroupAverageGrade(Long groupId, Long subjectId) {
        return journal.getGroupAverageGrade(groupId, subjectId);
    }

    public Journal getJournal() {
        return journal;
    }

    public void saveToExcel() throws IOException {
        // Создаем директорию, если её нет
        new File("data/excel").mkdirs();

        // Сохраняем предметы
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Subjects");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("description");
            headerRow.createCell(3).setCellValue("hours");

            int rowNum = 1;
            for (Subject subject : journal.getSubjects()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(subject.getId());
                row.createCell(1).setCellValue(subject.getName());
                row.createCell(2).setCellValue(subject.getDescription());
                row.createCell(3).setCellValue(subject.getHours());
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/subjects.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем группы
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Groups");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("year");
            headerRow.createCell(3).setCellValue("specialty");

            int rowNum = 1;
            for (Group group : journal.getGroups()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(group.getId());
                row.createCell(1).setCellValue(group.getName());
                row.createCell(2).setCellValue(group.getYear());
                row.createCell(3).setCellValue(group.getSpecialty());
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/groups.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем студентов
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Students");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("group_id");
            headerRow.createCell(3).setCellValue("birth_date");
            headerRow.createCell(4).setCellValue("contacts");

            int rowNum = 1;
            for (Student student : journal.getAllStudents()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(student.getId());
                row.createCell(1).setCellValue(student.getName());
                row.createCell(2).setCellValue(student.getGroup().getId());
                row.createCell(3).setCellValue(student.getBirthDate());
                row.createCell(4).setCellValue(student.getEmail());
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/students.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем преподавателей
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Teachers");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("name");
            headerRow.createCell(2).setCellValue("contacts");

            int rowNum = 1;
            Teacher teacher = journal.getTeacher();
            if (teacher != null) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(teacher.getId());
                row.createCell(1).setCellValue(teacher.getName());
                row.createCell(2).setCellValue(teacher.getContacts());
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/teachers.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем связи преподавателей с группами
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("TeacherGroups");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("teacher_id");
            headerRow.createCell(1).setCellValue("group_id");

            int rowNum = 1;
            Teacher teacher = journal.getTeacher();
            if (teacher != null) {
                for (Group group : teacher.getGroups()) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(teacher.getId());
                    row.createCell(1).setCellValue(group.getId());
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/teacher_groups.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем оценки
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Grades");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("student_id");
            headerRow.createCell(1).setCellValue("subject_id");
            headerRow.createCell(2).setCellValue("grade");
            headerRow.createCell(3).setCellValue("date");

            int rowNum = 1;
            for (Student student : journal.getAllStudents()) {
                for (Map.Entry<Long, List<Integer>> entry : student.getGrades().entrySet()) {
                    for (Integer grade : entry.getValue()) {
                        Row row = sheet.createRow(rowNum++);
                        row.createCell(0).setCellValue(student.getId());
                        row.createCell(1).setCellValue(entry.getKey());
                        row.createCell(2).setCellValue(grade);
                        row.createCell(3).setCellValue(LocalDate.now().toString()); // Текущая дата
                    }
                }
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/grades.xlsx")) {
                wb.write(fileOut);
            }
        }

        // Сохраняем расписание
        try (Workbook wb = new XSSFWorkbook()) {
            Sheet sheet = wb.createSheet("Schedule");
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("id");
            headerRow.createCell(1).setCellValue("group_id");
            headerRow.createCell(2).setCellValue("subject_id");
            headerRow.createCell(3).setCellValue("day_of_week");
            headerRow.createCell(4).setCellValue("start_time");
            headerRow.createCell(5).setCellValue("end_time");
            headerRow.createCell(6).setCellValue("room");

            int rowNum = 1;
            for (Schedule schedule : journal.getSchedule()) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(schedule.getId());
                row.createCell(1).setCellValue(schedule.getGroupId());
                row.createCell(2).setCellValue(schedule.getSubjectId());
                row.createCell(3).setCellValue(schedule.getDayOfWeek().toString());
                row.createCell(4).setCellValue(schedule.getStartTime().toString());
                row.createCell(5).setCellValue(schedule.getEndTime().toString());
                row.createCell(6).setCellValue(schedule.getRoom());
            }

            try (FileOutputStream fileOut = new FileOutputStream("data/excel/schedule.xlsx")) {
                wb.write(fileOut);
            }
        }
    }

    public List<Schedule> getSchedule() {
        return journal.getSchedule();
    }

    public List<Schedule> getGroupSchedule(Long groupId) {
        return journal.getGroupSchedule(groupId);
    }

    public List<Schedule> getSubjectSchedule(Long subjectId) {
        return journal.getSubjectSchedule(subjectId);
    }

    public void addSchedule(Long groupId, Long subjectId, DayOfWeek dayOfWeek, 
                          LocalTime startTime, LocalTime endTime, String room) throws IOException {
        // Генерируем новый ID
        Long newId = journal.getSchedule().stream()
            .mapToLong(Schedule::getId)
            .max()
            .orElse(0) + 1;

        Schedule schedule = new Schedule(newId, groupId, subjectId, dayOfWeek, startTime, endTime, room);
        journal.addSchedule(schedule);
        saveJournal();
    }

    public void removeSchedule(Long scheduleId) throws IOException {
        journal.removeSchedule(scheduleId);
        saveJournal();
    }
}
