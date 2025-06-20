package com.journal.model;

import java.time.DayOfWeek;
import java.time.LocalTime;

public class Schedule {
    private Long id;
    private Long groupId;
    private Long subjectId;
    private DayOfWeek dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;

    public Schedule() {}

    public Schedule(Long id, Long groupId, Long subjectId, DayOfWeek dayOfWeek, 
                   LocalTime startTime, LocalTime endTime, String room) {
        this.id = id;
        this.groupId = groupId;
        this.subjectId = subjectId;
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
        this.room = room;
    }

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public Long getSubjectId() { return subjectId; }
    public void setSubjectId(Long subjectId) { this.subjectId = subjectId; }
    public DayOfWeek getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }
    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }
    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    @Override
    public String toString() {
        return String.format("%s %s-%s %s", 
            dayOfWeek, startTime, endTime, room);
    }
}
