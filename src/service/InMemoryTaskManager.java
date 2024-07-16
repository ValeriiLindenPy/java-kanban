package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, Epic> epicTasks;
    private final HashMap<Integer, Subtask> subTasks;
    private final HistoryManager historyManager;
    private int taskCounter;

    public InMemoryTaskManager() {
        this.taskCounter = 1;
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Subtask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (Epic task : epicTasks.values()) {
            task.removeAllSubTasks();
            checkEpicStatus(task);
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        deleteSubTasks();
        epicTasks.clear();
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Subtask getSubTaskByID(int id) {
        Subtask task = subTasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic task = epicTasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public int createTask(Task task) {
        final int id = taskCounter;
        taskCounter++;
        task.setId(id);
        tasks.put(id, task);
        return id;
    }

    @Override
    public int createSubTask(Subtask task) {
        final int id = taskCounter;
        Epic epic = getEpicByID(task.getEpicId());
        if (epic == null) {
            System.out.println("No such epic");
            return -1;
        }
        task.setId(id);
        epic.addSubTask(id);
        checkEpicStatus(epic);
        subTasks.put(id, task);
        taskCounter++;
        return id;
    }

    @Override
    public int createEpicTask(Epic task) {
        final int id = taskCounter;
        task.setId(id);
        epicTasks.put(id, task);
        taskCounter++;
        return id;
    }

    @Override
    public void updateTask(Task task) {
        if (tasks.containsKey(task.getId())) {
            tasks.put(task.getId(), task);
        } else {
            System.out.println("No such task");
        }
    }

    @Override
    public void updateSubTask(Subtask task) {
        if (subTasks.containsKey(task.getId())) {
            subTasks.put(task.getId(), task);
            checkEpicStatus(getEpicByID(task.getEpicId()));
        } else {
            System.out.println("No such SubTask");
        }
    }

    @Override
    public void updateEpicTask(Epic task) {
        if (epicTasks.containsKey(task.getId())) {
            epicTasks.put(task.getId(), task);
            checkEpicStatus(task);
        } else {
            System.out.println("No such EpicTask");
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        if (tasks.containsKey(id)) {
            tasks.remove(id);
        } else {
            System.out.println("No such Task");
        }
    }

    @Override
    public void deleteSubTaskByID(int id) {
        if (subTasks.containsKey(id)) {
            Epic epic = getEpicByID(getSubTaskByID(id).getEpicId());
            epic.removeSubTaskbyId(id);
            subTasks.remove(id);
            checkEpicStatus(epic);
        } else {
            System.out.println("No such SubTask");
        }

    }

    @Override
    public void deleteEpicByID(int id) {
        ArrayList<Integer> subtasksID = getEpicByID(id).getAllSubtasks();

        for (Integer subtaskID : subtasksID) {
            deleteSubTaskByID(subtaskID);
        }

        if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
        } else {
            System.out.println("No such EpicTask");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubTasks(int epicID) {
        ArrayList<Integer> subtasksID = getEpicByID(epicID).getAllSubtasks();
        ArrayList<Subtask> subtasksList = new ArrayList<>();

        for (Integer subtaskID : subtasksID) {
            subtasksList.add(getSubTaskByID(subtaskID));
        }

        return subtasksList;
    }

    @Override
    public List<Task> getHistory() {
        return this.historyManager.getHistory();
    }

    void resetHistory() {
        this.historyManager.clearHistory();
    }


    //Проверка статуса Epic
    private void checkEpicStatus(Epic epic) {
        Set<String> epicSubTasks = new HashSet<>();
        for (Subtask epicSubTask : getEpicSubTasks(epic.getId())) {
            if (epicSubTask != null) {
                epicSubTasks.add(epicSubTask.getStatus().toString());
            }
        }

        if (epicSubTasks.size() == 1 && epicSubTasks.contains(Status.DONE.toString())) {
            epic.setStatus(Status.DONE);
        } else if (epicSubTasks.isEmpty() || (epicSubTasks.size() == 1 && epicSubTasks.contains(Status.NEW.toString()))) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    public void resetTaskCounter() {
        this.taskCounter = 1;
    }



}
