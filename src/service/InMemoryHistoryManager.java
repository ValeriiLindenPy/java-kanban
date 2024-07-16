package service;

import model.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> taskHistory;
    private static final int MAX_TASKS_IN_HISTORY = 10;

    public InMemoryHistoryManager() {
        this.taskHistory = new ArrayList<>(MAX_TASKS_IN_HISTORY);
    }

    @Override
    public void add(Task task) {
        if (this.taskHistory.size() >= MAX_TASKS_IN_HISTORY) {
            this.taskHistory.removeFirst();
            this.taskHistory.add(new Task(task.getId(), task.getName(), task.getDescription(),task.getStatus()));
        } else {
            this.taskHistory.add(new Task(task.getId(), task.getName(), task.getDescription(),task.getStatus()));
        }
    }

    @Override
    public List<Task> getHistory() {
        return this.taskHistory;
    }

    @Override
    public void clearHistory() {
        this.taskHistory.clear();
    }
}
