package model;



public class SubTask extends Task {
    protected int epicId;

    public SubTask(int id, String name, String description, Status status) {
        super(id, name, description, status);
    }

    public SubTask(String name, String description) {
        super(name, description);
    }


    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

}
