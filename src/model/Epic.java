package model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Epic extends Task {

    protected Set<Integer> subtasksIds;

    public Epic(int id, String name, String description, Status status) {
        super(id, name, description, status);
        this.subtasksIds = new HashSet<>();
    }

    public Epic(String name, String description) {
        super(name, description);
        this.subtasksIds = new HashSet<>();
    }

    public ArrayList<Integer> getAllSubtasks() {return new ArrayList<>(subtasksIds);}

    public String addSubTask(int subTaskId) {
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
