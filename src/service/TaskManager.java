package service;

import model.EpicTask;
import model.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class TaskManager implements ITaskmanager {
    private final HashMap<Integer, Task> tasks;
    private final HashMap<Integer, EpicTask> epicTasks;
    private final HashMap<Integer, SubTask> subTasks;
    private int taskCounter;

    public TaskManager() {
        this.taskCounter = 1;
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<SubTask> getSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public ArrayList<EpicTask> getEpics() {
        return new ArrayList<>(epicTasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        for (EpicTask task : epicTasks.values()) {
            task.removeAllSubTasks();
            checkEpicStatus(task);
        }
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        epicTasks.clear();
    }

    @Override
    public Task getTaskByID(int id) {
        return tasks.get(id);
    }

    @Override
    public SubTask getSubTaskByID(int id) {
        return subTasks.get(id);
    }

    @Override
    public EpicTask getEpicByID(int id) {
        return epicTasks.get(id);
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
    public int createSubTask(SubTask task) {
        final int id = taskCounter;
        EpicTask epic = getEpicByID(task.getEpicId());
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
    public int createEpicTask(EpicTask task) {
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
    public void updateSubTask(SubTask task) {
        if (subTasks.containsKey(task.getId())) {
            subTasks.put(task.getId(), task);
            checkEpicStatus(getEpicByID(task.getEpicId()));
        } else {
            System.out.println("No such SubTask");
        }
    }
    @Override
    public void updateEpicTask(EpicTask task) {
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
            EpicTask epic = getEpicByID(getSubTaskByID(id).getEpicId());
            epic.removeSubTaskbyId(id);
            subTasks.remove(id);
            checkEpicStatus(epic);
        } else {
            System.out.println("No such SubTask");
        }

    }

    @Override
    public void deleteEpicByID(int id) {
        if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
        } else {
            System.out.println("No such EpicTask");
        }

    }

    @Override
    public ArrayList<SubTask> getEpicSubTasks(int epicID) {
        ArrayList<SubTask> subTaskOfEpic = new ArrayList<>();
        for (SubTask subtask : subTasks.values()) {
            if (subtask.getEpicId() == epicID) {
                subTaskOfEpic.add(subtask);
            }
        }
        return subTaskOfEpic;
    }

    //Проверка статуса Epic
    private void checkEpicStatus(EpicTask epic) {
        Set<String> epicSubTasks = new HashSet<>();
        for (SubTask epicSubTask : getEpicSubTasks(epic.getId())) {
            epicSubTasks.add(epicSubTask.getStatus().toString());
        }

        if (epicSubTasks.size() == 1 && epicSubTasks.contains(Status.DONE.toString())) {
            epic.setStatus(Status.DONE);
        } else if (epicSubTasks.isEmpty()
                || (epicSubTasks.size() == 1
                & epicSubTasks.contains(Status.NEW.toString()))) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }


    public int getTaskCounter() {
        return taskCounter;
    }
}
