package model;

import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    protected int epicId;

    public Subtask(int id, String name, String description, Status status, LocalDateTime startTime, Duration duration, int epicId) {
        super(id, name, description, status,startTime, duration);
        this.epicId = epicId;
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public Subtask(String name, String description, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
    }

    public Subtask(int id, String name, String description, Status status, int epicId) {
        super(id, name, description, status);
        this.epicId = epicId;
    }

    @Override
    public Type getType() {
        return Type.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException("Subtask should not be able to add itself as a Epic.");
        }
        this.epicId = epicId;
    }
}
