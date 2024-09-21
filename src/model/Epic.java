package model;

import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {
    public void setSubtasksIds(Set<Integer> subtasksIds) {
        this.subtasksIds = subtasksIds;
    }

    private Set<Integer> subtasksIds = new HashSet<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration) {
        super(id, name, description, status, startTime, duration);
    }

    public Epic(int id, String name, String description, Status status) {
        this(id, name, description, status, null, null);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    @Override
    public Type getType() {
        return Type.EPIC;
    }

    public ArrayList<Integer> getAllSubtasks() {
        return new ArrayList<>(subtasksIds);
    }

    public String addSubTask(int subTaskId) {

        if (subTaskId == this.getId()) {
            throw new IllegalArgumentException("Epic should not be able to add itself as a subtask.");
        }

        this.subtasksIds.add(subTaskId);
        return "Subtask added in Epic ID " + this.getId();
    }

    public String removeSubTaskbyId(int id) {
        if (this.subtasksIds.contains(id)) {
            this.subtasksIds.remove(id);
            return "Subtask ID " + id + "has been deleted from Epic ID "
                    + this.getId();
        }
        return "Subtask has notbeen found in Epic ID " +
                this.getId();
    }

    public void removeAllSubTasks() {
        subtasksIds.clear();
    }

    @Override
    public LocalDateTime getEndTime() {
        return this.endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "EpicTask{" +
                "subtasksIds=" + subtasksIds +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
