package model;

public class Subtask extends Task {
    protected int epicId;

    public Subtask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public Subtask(String name, String description) {
        super(name, description);
    }

    public int getEpicId() {return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            throw new IllegalArgumentException("Subtask should not be able to add itself as a Epic.");
        }
        this.epicId = epicId;
    }
}
