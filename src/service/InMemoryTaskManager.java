package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import service.interfaces.HistoryManager;
import service.interfaces.TaskManager;
import service.utils.TasksIntersectionValidator;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final Map<Integer, Task> tasks;
    protected final Map<Integer, Epic> epicTasks;
    protected final Map<Integer, Subtask> subTasks;

    protected final TreeSet<Task> orderedTasks;

    private final HistoryManager historyManager;
    protected int taskCounter;

    public InMemoryTaskManager() {
        this.taskCounter = 1;
        this.tasks = new HashMap<>();
        this.subTasks = new HashMap<>();
        this.epicTasks = new HashMap<>();
        this.historyManager = Managers.getDefaultHistory();
        this.orderedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime));

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
        List<Task> tasksToRemove = new ArrayList<>(tasks.values());
        tasksToRemove.forEach(orderedTasks::remove);
        tasks.clear();
    }

    @Override
    public void deleteSubTasks() {
        epicTasks.values().forEach(task -> {
            task.removeAllSubTasks();
            checkEpicStatus(task);
            orderedTasks.remove(task);
            checkEpicTime(task);
        });
        subTasks.clear();
    }

    @Override
    public void deleteEpics() {
        deleteSubTasks();
        epicTasks.clear();
    }

    @Override
    public Optional<Task> getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Subtask> getSubTaskByID(int id) {
        Subtask task = subTasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Epic> getEpicByID(int id) {
        Epic task = epicTasks.get(id);
        if (task != null) {
            historyManager.add(task);
            return Optional.of(task);
        }
        return Optional.empty();
    }

    @Override
    public int createTask(Task task) {
        final int id = taskCounter;
        taskCounter++;
        task.setId(id);
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            boolean isValid = getPrioritizedTasks().stream()
                    .allMatch(t -> TasksIntersectionValidator.isValid(t, task));
            if (isValid) {
                orderedTasks.add(task);
            }
        }
        return id;
    }

    @Override
    public int createSubTask(Subtask task) {
        final int id = taskCounter;
        Epic epic = getEpicByID(task.getEpicId()).get();

        if (epic == null) {
            throw new IllegalArgumentException("Epic with ID " + task.getEpicId() + " does not exist.");
        }

        task.setId(id);
        epic.addSubTask(id);
        checkEpicStatus(epic);
        subTasks.put(id, task);
        if (task.getStartTime() != null) {
            checkEpicTime(epic);
            boolean isValid = getPrioritizedTasks().stream()
                    .allMatch(t -> TasksIntersectionValidator.isValid(t, task));
            if (isValid) {
                orderedTasks.add(task);
            }
        }
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
            if (orderedTasks.contains(task)) {
                orderedTasks.remove(task);
                boolean isValid = getPrioritizedTasks().stream()
                        .allMatch(t -> TasksIntersectionValidator.isValid(t, task));
                if (isValid) {
                    orderedTasks.add(task);
                }
            }
        } else {
            System.out.println("No such task");
        }
    }

    @Override
    public void updateSubTask(Subtask task) {
        if (subTasks.containsKey(task.getId())) {
            Subtask oldTask = subTasks.get(task.getId());
            subTasks.put(task.getId(), task);
            if (orderedTasks.contains(oldTask)) {
                orderedTasks.remove(oldTask);
                boolean isValid = getPrioritizedTasks().stream()
                        .allMatch(t -> TasksIntersectionValidator.isValid(t, task));
                if (isValid) {
                    orderedTasks.add(task);
                    checkEpicTime(getEpicByID(task.getEpicId()).get());
                }
            }
            checkEpicStatus(getEpicByID(task.getEpicId()).get());
        } else {
            System.out.println("No such SubTask");
        }
    }

    @Override
    public void updateEpicTask(Epic task) {
        if (epicTasks.containsKey(task.getId())) {
            epicTasks.put(task.getId(), task);
            checkEpicStatus(task);
            checkEpicTime(task);
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
            Epic epic = getEpicByID(getSubTaskByID(id).get().getEpicId()).get();
            epic.removeSubTaskbyId(id);
            subTasks.remove(id);
            checkEpicStatus(epic);
            checkEpicTime(epic);
        } else {
            System.out.println("No such SubTask");
        }

    }

    @Override
    public void deleteEpicByID(int id) {
        ArrayList<Integer> subtasksID = getEpicByID(id).get().getAllSubtasks();

        subtasksID.forEach(this::deleteSubTaskByID);

        if (epicTasks.containsKey(id)) {
            epicTasks.remove(id);
        } else {
            System.out.println("No such EpicTask");
        }
    }

    @Override
    public ArrayList<Subtask> getEpicSubTasks(int epicID) {
        return (ArrayList<Subtask>) getEpicByID(epicID).get().getAllSubtasks().stream()
                .map(this::getSubTaskByID)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(orderedTasks);
    }

    //Проверка статуса Epic
    private void checkEpicStatus(Epic epic) {
        Set<String> epicSubTasks = getEpicSubTasks(epic.getId()).stream()
                .filter(Objects::nonNull)
                .map(task -> task.getStatus().toString())
                .collect(Collectors.toSet());

        if (epicSubTasks.size() == 1 && epicSubTasks.contains(Status.DONE.toString())) {
            epic.setStatus(Status.DONE);
        } else if (epicSubTasks.isEmpty() || (epicSubTasks.size() == 1
                && epicSubTasks.contains(Status.NEW.toString()))) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    private void checkEpicTime(Epic epic) {
        TreeSet<Subtask> subtasksOrderedByStartTime =
                new TreeSet<>(Comparator.comparing(Subtask::getStartTime));
        TreeSet<Subtask> subtasksOrderedByEndTime =
                new TreeSet<>(Comparator.comparing(Subtask::getEndTime));

        long epicDuration = getEpicSubTasks(epic.getId()).stream()
                .map(Subtask::getDuration)
                .filter(Objects::nonNull)
                .mapToLong(Duration::toMinutes)
                .sum();

        getEpicSubTasks(epic.getId()).forEach(sub -> {
            if (sub.getStartTime() != null) {
                subtasksOrderedByStartTime.add(sub);
            }

            if (sub.getEndTime() != null) {
                subtasksOrderedByEndTime.add(sub);
            }

        });

        epic.setDuration(Duration.ofMinutes(epicDuration));
        if (!subtasksOrderedByStartTime.isEmpty()) {
            epic.setStartTime(subtasksOrderedByStartTime.first().getStartTime());
        } else {
            epic.setStartTime(null);
        }

        if (!subtasksOrderedByEndTime.isEmpty()) {
            epic.setEndTime(subtasksOrderedByEndTime.last().getEndTime());
        } else {
            epic.setEndTime(null);
        }
    }
}
