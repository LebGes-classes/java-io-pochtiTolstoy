package com.journal.ui;

import com.journal.model.Student;
import com.journal.model.Subject;
import com.journal.model.Group;
import com.journal.model.Schedule;
import com.journal.service.JournalService;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.HashMap;
import java.util.ArrayList;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.Comparator;
import java.util.stream.Collectors;

public class ConsoleUI {
    private final JournalService journalService;
    private final Scanner scanner;

    public ConsoleUI(JournalService journalService) {
        this.journalService = journalService;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        while (true) {
            printMenu();
            int choice = readInt("Выберите действие: ");
            
            switch (choice) {
                case 1 -> showStudents();
                case 2 -> addGrade();
                case 3 -> showStudentPerformance();
                case 4 -> showGroupPerformance();
                case 5 -> saveToExcel();
                case 6 -> showStudentRating();
                case 7 -> addStudent();
                case 8 -> deleteStudent();
                case 9 -> showGroupSchedule();
                case 10 -> addSchedule();
                case 11 -> removeSchedule();
                case 0 -> {
                    System.out.println("До свидания!");
                    return;
                }
                default -> System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private void printMenu() {
        System.out.println("\nМеню:");
        System.out.println("1. Показать список студентов");
        System.out.println("2. Добавить оценку");
        System.out.println("3. Успеваемость студента");
        System.out.println("4. Успеваемость группы");
        System.out.println("5. Сохранить данные в Excel");
        System.out.println("6. Рейтинг студентов");
        System.out.println("7. Добавить студента");
        System.out.println("8. Удалить студента");
        System.out.println("9. Расписание группы");
        System.out.println("10. Добавить занятие");
        System.out.println("11. Удалить занятие");
        System.out.println("0. Выход");
    }

    private void showStudents() {
        List<Student> students = journalService.getStudents();
        List<Subject> subjects = journalService.getSubjects();
        // Формируем карту предметов для быстрого доступа по id
        java.util.Map<Long, Subject> subjectMap = new java.util.HashMap<>();
        for (Subject s : subjects) subjectMap.put(s.getId(), s);
        System.out.println("\nСписок студентов:");
        for (Student student : students) {
            System.out.println(student.toPrettyString(subjectMap));
            System.out.println();
        }
    }

    private void addGrade() {
        // Показываем список студентов
        List<Student> students = journalService.getStudents();
        System.out.println("\nВыберите студента:");
        System.out.println("----------------");
        for (Student student : students) {
            System.out.printf("%d. %s (Группа: %s)%n", 
                student.getId(), 
                student.getName(), 
                student.getGroup().getName());
        }
        
        // Выбираем студента
        Long studentId = null;
        while (studentId == null) {
            int choice = readInt("\nВведите ID студента: ");
            studentId = students.stream()
                .filter(s -> s.getId() == choice)
                .map(Student::getId)
                .findFirst()
                .orElse(null);
            if (studentId == null) {
                System.out.println("Студент с таким ID не найден. Попробуйте снова.");
            }
        }

        // Показываем список предметов
        List<Subject> subjects = journalService.getSubjects();
        System.out.println("\nВыберите предмет:");
        System.out.println("----------------");
        for (Subject subject : subjects) {
            System.out.printf("%d. %s%n", 
                subject.getId(), 
                subject.getName());
        }
        
        // Выбираем предмет
        Long subjectId = null;
        while (subjectId == null) {
            int choice = readInt("\nВведите ID предмета: ");
            subjectId = subjects.stream()
                .filter(s -> s.getId() == choice)
                .map(Subject::getId)
                .findFirst()
                .orElse(null);
            if (subjectId == null) {
                System.out.println("Предмет с таким ID не найден. Попробуйте снова.");
            }
        }

        // Вводим оценку (2-5)
        int grade = -1;
        while (grade < 2 || grade > 5) {
            grade = readInt("\nВведите оценку (2-5): ");
            if (grade < 2 || grade > 5) {
                System.out.println("Ошибка: оценка должна быть от 2 до 5 включительно. Попробуйте снова.");
            }
        }

        try {
            journalService.addGrade(studentId, subjectId, grade);
            System.out.println("\nОценка успешно добавлена!");
            
            // Показываем средний балл
            double average = journalService.getStudentAverageGrade(studentId, subjectId);
            System.out.printf("Средний балл студента по предмету: %.2f%n", average);
        } catch (IOException e) {
            System.err.println("\nОшибка при добавлении оценки: " + e.getMessage());
        }
    }

    private void showStudentPerformance() {
        // Показываем список студентов
        List<Student> students = journalService.getStudents();
        System.out.println("\nВыберите студента:");
        System.out.println("----------------");
        for (Student student : students) {
            System.out.printf("%d. %s (Группа: %s)%n", 
                student.getId(), 
                student.getName(), 
                student.getGroup().getName());
        }
        
        // Выбираем студента
        Student selectedStudent = null;
        while (selectedStudent == null) {
            int choice = readInt("\nВведите ID студента: ");
            selectedStudent = students.stream()
                .filter(s -> s.getId() == choice)
                .findFirst()
                .orElse(null);
            if (selectedStudent == null) {
                System.out.println("Студент с таким ID не найден. Попробуйте снова.");
            }
        }

        // Получаем карту предметов
        List<Subject> subjects = journalService.getSubjects();
        Map<Long, Subject> subjectMap = new HashMap<>();
        for (Subject s : subjects) subjectMap.put(s.getId(), s);

        // Выводим оценки и средние баллы по предметам
        System.out.println("\nУспеваемость студента:");
        System.out.println(selectedStudent.toPrettyString(subjectMap));

        // Общий средний балл
        double totalSum = 0;
        int subjectsCount = 0;
        if (selectedStudent.getGrades() != null) {
            for (var entry : selectedStudent.getGrades().entrySet()) {
                List<Integer> grades = entry.getValue();
                if (!grades.isEmpty()) {
                    double subjectAvg = grades.stream()
                        .mapToInt(Integer::intValue)
                        .average()
                        .orElse(0.0);
                    totalSum += subjectAvg;
                    subjectsCount++;
                }
            }
        }
        if (subjectsCount > 0) {
            System.out.printf("\nОбщий средний балл: %.2f\n", totalSum / subjectsCount);
        } else {
            System.out.println("\nНет оценок для вычисления общего среднего балла.");
        }
    }

    private Group selectGroup() {
        List<Group> groups = journalService.getGroups();
        System.out.println("\nВыберите группу:");
        System.out.println("----------------");
        for (Group group : groups) {
            System.out.printf("%d. %s (%s)%n", 
                group.getId(), 
                group.getName(), 
                group.getSpecialty());
        }
        
        while (true) {
            int choice = readInt("\nВведите ID группы: ");
            Group group = groups.stream()
                .filter(g -> g.getId() == choice)
                .findFirst()
                .orElse(null);
            if (group != null) {
                return group;
            }
            System.out.println("Группа с таким ID не найдена. Попробуйте снова.");
        }
    }

    private void showGroupPerformance() {
        // Выбираем группу
        Group selectedGroup = selectGroup();

        // Получаем карту предметов
        List<Subject> subjects = journalService.getSubjects();
        Map<Long, Subject> subjectMap = new HashMap<>();
        for (Subject s : subjects) subjectMap.put(s.getId(), s);

        // Получаем студентов группы
        List<Student> groupStudents = journalService.getStudents().stream()
            .filter(s -> s.getGroup().getId() == selectedGroup.getId())
            .toList();

        // Выводим информацию о группе
        System.out.println("\nГруппа: " + selectedGroup.getName());
        System.out.println("Специальность: " + selectedGroup.getSpecialty());
        System.out.println("\nСтуденты:");
        for (Student student : groupStudents) {
            System.out.printf("%d. %s%n", student.getId(), student.getName());
        }

        // Статистика по предметам
        System.out.println("\nУспеваемость по предметам:");
        for (Subject subject : subjects) {
            System.out.println(subject.getName() + ":");
            
            // Собираем все оценки по предмету
            List<Integer> allGrades = new ArrayList<>();
            Map<Student, Double> studentAverages = new HashMap<>();
            
            for (Student student : groupStudents) {
                List<Integer> grades = student.getGrades().getOrDefault(subject.getId(), new ArrayList<>());
                if (!grades.isEmpty()) {
                    allGrades.addAll(grades);
                    double avg = grades.stream().mapToInt(Integer::intValue).average().orElse(0);
                    studentAverages.put(student, avg);
                }
            }

            if (allGrades.isEmpty()) {
                System.out.println("  Нет оценок");
                continue;
            }

            // Средний балл группы по предмету
            double groupAvg = allGrades.stream().mapToInt(Integer::intValue).average().orElse(0);
            System.out.printf("  Средний балл: %.2f ", groupAvg);

            // Процент успеваемости (4-5)
            long goodGrades = allGrades.stream().filter(g -> g >= 4).count();
            double successRate = (double) goodGrades / allGrades.size() * 100;
            System.out.printf("  Успеваемость: %.1f%% ", successRate);

            // Процент качества (5)
            long excellentGrades = allGrades.stream().filter(g -> g == 5).count();
            double qualityRate = (double) excellentGrades / allGrades.size() * 100;
            System.out.printf("  Качество: %.1f%% ", qualityRate);

            // Лучший студент
            if (!studentAverages.isEmpty()) {
                Student bestStudent = studentAverages.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);
                if (bestStudent != null) {
                    System.out.printf("  Лучший студент: %s (%.1f)%n", 
                        bestStudent.getName(), 
                        studentAverages.get(bestStudent));
                }
            }
            System.out.println();
        }

        // Общий средний балл группы
        double totalSum = 0;
        int totalCount = 0;
        for (Student student : groupStudents) {
            for (List<Integer> grades : student.getGrades().values()) {
                for (int grade : grades) {
                    totalSum += grade;
                    totalCount++;
                }
            }
        }
        if (totalCount > 0) {
            System.out.printf("\nОбщий средний балл группы: %.2f%n", totalSum / totalCount);
        }

        // Рейтинг студентов
        System.out.println("\nРейтинг студентов:");
        Map<Student, Double> studentTotalAverages = new HashMap<>();
        for (Student student : groupStudents) {
            double sum = 0;
            int count = 0;
            for (List<Integer> grades : student.getGrades().values()) {
                for (int grade : grades) {
                    sum += grade;
                    count++;
                }
            }
            if (count > 0) {
                studentTotalAverages.put(student, sum / count);
            }
        }

        // Сортируем студентов по среднему баллу
        studentTotalAverages.entrySet().stream()
            .sorted(Map.Entry.<Student, Double>comparingByValue().reversed())
            .forEach(entry -> System.out.printf("%s - %.2f%n", 
                entry.getKey().getName(), 
                entry.getValue()));
    }

    private void saveToExcel() {
        try {
            System.out.println("\nСохранение данных в Excel...");
            journalService.saveToExcel();
            System.out.println("Данные успешно сохранены в Excel файлы:");
            System.out.println("- subjects.xlsx");
            System.out.println("- groups.xlsx");
            System.out.println("- students.xlsx");
            System.out.println("- teachers.xlsx");
            System.out.println("- teacher_groups.xlsx");
            System.out.println("- grades.xlsx");
            System.out.println("- schedule.xlsx");
        } catch (IOException e) {
            System.err.println("\nОшибка при сохранении данных: " + e.getMessage());
        }
    }

    private void showStudentRating() {
        System.out.println("\nРейтинг студентов по среднему баллу:");
        System.out.println("--------------------------------");

        // Получаем всех студентов и их средние баллы
        Map<Student, Double> studentAverages = new HashMap<>();
        for (Student student : journalService.getStudents()) {
            double totalAverage = 0;
            int subjectsCount = 0;
            
            // Считаем средний балл по всем предметам
            for (Map.Entry<Long, List<Integer>> entry : student.getGrades().entrySet()) {
                double subjectAverage = entry.getValue().stream()
                    .mapToInt(Integer::intValue)
                    .average()
                    .orElse(0.0);
                totalAverage += subjectAverage;
                subjectsCount++;
            }
            
            // Если у студента есть оценки, добавляем его в рейтинг
            if (subjectsCount > 0) {
                studentAverages.put(student, totalAverage / subjectsCount);
            }
        }

        // Сортируем и выводим студентов
        studentAverages.entrySet().stream()
            .sorted(Map.Entry.<Student, Double>comparingByValue().reversed())
            .forEach(entry -> {
                Student student = entry.getKey();
                double average = entry.getValue();
                System.out.printf("%s (Группа: %s) - %.2f%n", 
                    student.getName(),
                    student.getGroup().getName(),
                    average);
            });
    }

    private void addStudent() {
        System.out.println("\nДобавление нового студента");
        System.out.println("------------------------");

        // Выбираем группу
        Group selectedGroup = selectGroup();
        if (selectedGroup == null) {
            System.out.println("Ошибка: не удалось выбрать группу");
            return;
        }

        // Вводим данные студента
        System.out.print("Введите имя студента: ");
        String name = scanner.nextLine();

        System.out.print("Введите email студента: ");
        String email = scanner.nextLine();

        System.out.print("Введите дату рождения (YYYY-MM-DD): ");
        String birthDate = scanner.nextLine();

        // Генерируем новый ID (максимальный существующий + 1)
        Long newId = journalService.getStudents().stream()
            .mapToLong(Student::getId)
            .max()
            .orElse(0) + 1;

        // Создаем студента
        Student newStudent = new Student(newId, name, birthDate, email);
        newStudent.setGroup(selectedGroup);
        selectedGroup.addStudent(newStudent);

        try {
            journalService.saveJournal();
            System.out.println("\nСтудент успешно добавлен!");
            System.out.println(newStudent.toPrettyString(journalService.getSubjects().stream()
                .collect(HashMap::new, (m, s) -> m.put(s.getId(), s), HashMap::putAll)));
        } catch (IOException e) {
            System.err.println("\nОшибка при сохранении данных: " + e.getMessage());
        }
    }

    private void deleteStudent() {
        System.out.println("\nУдаление студента");
        System.out.println("----------------");

        // Показываем список студентов
        List<Student> students = journalService.getStudents();
        System.out.println("\nВыберите студента для удаления:");
        for (Student student : students) {
            System.out.printf("%d. %s (Группа: %s)%n", 
                student.getId(), 
                student.getName(), 
                student.getGroup().getName());
        }

        // Выбираем студента
        Student selectedStudent = null;
        while (selectedStudent == null) {
            int choice = readInt("\nВведите ID студента: ");
            selectedStudent = students.stream()
                .filter(s -> s.getId() == choice)
                .findFirst()
                .orElse(null);
            if (selectedStudent == null) {
                System.out.println("Студент с таким ID не найден. Попробуйте снова.");
            }
        }

        // Подтверждение удаления
        System.out.printf("\nВы уверены, что хотите удалить студента %s? (да/нет): ", selectedStudent.getName());
        String confirmation = scanner.nextLine().toLowerCase();
        if (!confirmation.equals("да")) {
            System.out.println("Удаление отменено.");
            return;
        }

        // Удаляем студента из группы
        Group group = selectedStudent.getGroup();
        group.getStudents().remove(selectedStudent);
        selectedStudent.setGroup(null);

        try {
            journalService.saveJournal();
            System.out.println("\nСтудент успешно удален!");
        } catch (IOException e) {
            System.err.println("\nОшибка при сохранении данных: " + e.getMessage());
        }
    }

    private void showGroupSchedule() {
        System.out.println("\nРасписание группы");
        System.out.println("----------------");

        // Выбираем группу
        Group selectedGroup = selectGroup();
        if (selectedGroup == null) {
            System.out.println("Ошибка: не удалось выбрать группу");
            return;
        }

        // Получаем расписание группы
        List<Schedule> schedule = journalService.getGroupSchedule(selectedGroup.getId());
        if (schedule.isEmpty()) {
            System.out.println("Расписание пусто");
            return;
        }

        // Получаем карту предметов для отображения названий
        Map<Long, Subject> subjectMap = journalService.getSubjects().stream()
            .collect(HashMap::new, (m, s) -> m.put(s.getId(), s), HashMap::putAll);

        // Группируем занятия по дням недели
        Map<DayOfWeek, List<Schedule>> scheduleByDay = schedule.stream()
            .collect(Collectors.groupingBy(Schedule::getDayOfWeek));

        // Выводим расписание по дням
        for (DayOfWeek day : DayOfWeek.values()) {
            List<Schedule> daySchedule = scheduleByDay.getOrDefault(day, new ArrayList<>());
            if (!daySchedule.isEmpty()) {
                System.out.println("\n" + day + ":");
                daySchedule.sort(Comparator.comparing(Schedule::getStartTime));
                for (Schedule s : daySchedule) {
                    String subjectName = subjectMap.containsKey(s.getSubjectId()) 
                        ? subjectMap.get(s.getSubjectId()).getName() 
                        : "ID=" + s.getSubjectId();
                    System.out.printf("  %s-%s %s (%s)%n", 
                        s.getStartTime(), s.getEndTime(), subjectName, s.getRoom());
                }
            }
        }
    }

    private void addSchedule() {
        System.out.println("\nДобавление занятия");
        System.out.println("-----------------");

        // Выбираем группу
        Group selectedGroup = selectGroup();
        if (selectedGroup == null) {
            System.out.println("Ошибка: не удалось выбрать группу");
            return;
        }

        // Выбираем предмет
        List<Subject> subjects = journalService.getSubjects();
        System.out.println("\nВыберите предмет:");
        for (Subject subject : subjects) {
            System.out.printf("%d. %s%n", subject.getId(), subject.getName());
        }

        Long subjectId = null;
        while (subjectId == null) {
            int choice = readInt("\nВведите ID предмета: ");
            subjectId = subjects.stream()
                .filter(s -> s.getId() == choice)
                .map(Subject::getId)
                .findFirst()
                .orElse(null);
            if (subjectId == null) {
                System.out.println("Предмет с таким ID не найден. Попробуйте снова.");
            }
        }

        // Выбираем день недели
        System.out.println("\nВыберите день недели:");
        DayOfWeek[] days = DayOfWeek.values();
        for (int i = 0; i < days.length; i++) {
            System.out.printf("%d. %s%n", i + 1, days[i]);
        }

        int dayChoice = -1;
        while (dayChoice < 1 || dayChoice > days.length) {
            dayChoice = readInt("\nВведите номер дня: ");
            if (dayChoice < 1 || dayChoice > days.length) {
                System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
        DayOfWeek dayOfWeek = days[dayChoice - 1];

        // Вводим время начала
        System.out.print("\nВведите время начала (HH:mm): ");
        String startTimeStr = scanner.nextLine();
        LocalTime startTime = LocalTime.parse(startTimeStr);

        // Вводим время окончания
        System.out.print("Введите время окончания (HH:mm): ");
        String endTimeStr = scanner.nextLine();
        LocalTime endTime = LocalTime.parse(endTimeStr);

        // Вводим аудиторию
        System.out.print("Введите номер аудитории: ");
        String room = scanner.nextLine();

        try {
            journalService.addSchedule(selectedGroup.getId(), subjectId, dayOfWeek, 
                                     startTime, endTime, room);
            System.out.println("\nЗанятие успешно добавлено!");
        } catch (IOException e) {
            System.err.println("\nОшибка при сохранении данных: " + e.getMessage());
        }
    }

    private void removeSchedule() {
        System.out.println("\nУдаление занятия");
        System.out.println("----------------");

        // Выбираем группу
        Group selectedGroup = selectGroup();
        if (selectedGroup == null) {
            System.out.println("Ошибка: не удалось выбрать группу");
            return;
        }

        // Получаем расписание группы
        List<Schedule> schedule = journalService.getGroupSchedule(selectedGroup.getId());
        if (schedule.isEmpty()) {
            System.out.println("Расписание пусто");
            return;
        }

        // Получаем карту предметов для отображения названий
        Map<Long, Subject> subjectMap = journalService.getSubjects().stream()
            .collect(HashMap::new, (m, s) -> m.put(s.getId(), s), HashMap::putAll);

        // Выводим список занятий
        System.out.println("\nВыберите занятие для удаления:");
        for (Schedule s : schedule) {
            String subjectName = subjectMap.containsKey(s.getSubjectId()) 
                ? subjectMap.get(s.getSubjectId()).getName() 
                : "ID=" + s.getSubjectId();
            System.out.printf("%d. %s %s-%s %s%n", 
                s.getId(), s.getDayOfWeek(), s.getStartTime(), s.getEndTime(), 
                subjectName, s.getRoom());
        }

        // Выбираем занятие
        Long scheduleId = null;
        while (scheduleId == null) {
            int choice = readInt("\nВведите ID занятия: ");
            scheduleId = schedule.stream()
                .filter(s -> s.getId() == choice)
                .map(Schedule::getId)
                .findFirst()
                .orElse(null);
            if (scheduleId == null) {
                System.out.println("Занятие с таким ID не найдено. Попробуйте снова.");
            }
        }

        try {
            journalService.removeSchedule(scheduleId);
            System.out.println("\nЗанятие успешно удалено!");
        } catch (IOException e) {
            System.err.println("\nОшибка при сохранении данных: " + e.getMessage());
        }
    }

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите число.");
            }
        }
    }
}
