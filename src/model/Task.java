package model;

import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task {
    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected Duration duration;
    protected LocalDateTime startTime;

    // Базовый конструктор с полным набором параметров
    public Task(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
        this.startTime = startTime;
        this.duration = duration;
    }

    // Конструктор без id
    public Task(String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        this(0, name, description, status, startTime, duration);
    }

    // Конструктор без времени начала и продолжительности, только с состоянием
    public Task(int id, String name, String description, Status status) {
        this(id, name, description, status, null, null);
    }

    // Конструктор только с временем начала и продолжительностью, статус по умолчанию NEW
    public Task(String name, String description, Duration duration, LocalDateTime startTime) {
        this(0, name, description, Status.NEW, startTime, duration);
    }

    // Самый простой конструктор, статус по умолчанию NEW
    public Task(String name, String description) {
        this(0, name, description, Status.NEW, null, null);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return Type.TASK;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, status);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", duration=" + duration +
                ", startTime=" + startTime +
                '}';
    }
}
