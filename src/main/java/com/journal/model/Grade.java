package com.journal.model;

public class Grade {
    private Long studentId;
    private Long subjectId;
    private Integer grade;
    private String date;

    public Grade(Long studentId, Long subjectId, Integer grade, String date) {
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.grade = grade;
        this.date = date;
    }

    // Геттеры и сеттеры
    public Long getStudentId() { return studentId; }
    public void setStudentId(Long studentId) { this.studentId = studentId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public Integer getGrade() { return grade; }
    public void setGrade(Integer grade) { this.grade = grade; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    @Override
    public String toString() {
        return "Grade{studentId=" + studentId + ", subjectId=" + subjectId + 
               ", grade=" + grade + ", date='" + date + "'}";
    }
}
