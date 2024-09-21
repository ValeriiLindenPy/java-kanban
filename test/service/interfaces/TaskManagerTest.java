package service.interfaces;

import model.Epic;
import model.enums.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.utils.TasksIntersectionValidator;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;
    protected File file;
    protected Task task1;
    protected Task task2;
    protected Task simpleTask;
    protected Subtask subtask1;
    protected Subtask subtask2;
    protected Epic epic;

    protected abstract T createTaskManager();

    @BeforeEach
    void setInitState() throws IOException {
        file = File.createTempFile("temp", ".csv");
        manager = createTaskManager();
        LocalDateTime startTime =
                LocalDateTime.of(2024, 6, 11, 14, 9);
        Duration duration = Duration.ofMinutes(20);

        simpleTask = new Task("simpleTask 1", "simpleTask Description 1");
        task1 = new Task("Task 1", "Description 1", duration, startTime);
        task2 = new Task("Task 2", "Description 2", duration.plusHours(1), startTime.plusHours(1));
        epic = new Epic("Epic 1", "Epic Description 1");
        subtask1 = new Subtask("Subtask 1", "Subtask Description 1", duration.plusHours(2), startTime.plusHours(2));
        subtask2 = new Subtask("Subtask 2", "Subtask Description 2", duration.plusHours(3), startTime.plusHours(3).plusDays(1));
    }

    @Test
    void getTasks() throws IntersectionTaskException {
        manager.createTask(task1);
        manager.createTask(task2);


        assertEquals(manager.getTasks(), new ArrayList<>(Arrays.asList(task1, task2)));
    }

    @Test
    void getSubTasks() throws IntersectionTaskException {
        int epicID = manager.createEpicTask(epic);
        subtask1.setEpicId(epicID);
        subtask2.setEpicId(epicID);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(manager.getSubTasks(), new ArrayList<>(Arrays.asList(subtask1, subtask2)));
    }

    @Test
    void getEpics() {
        manager.createEpicTask(epic);
        assertEquals(manager.getEpics().getFirst(), epic);
    }

    @Test
    void deleteTasks() throws IntersectionTaskException {
        manager.createTask(task1);
        manager.createTask(task2);

        manager.deleteTasks();

        assertEquals(manager.getTasks(), new ArrayList<Task>());
    }

    @Test
    void deleteSubTasks() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);
        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);
        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        manager.deleteSubTasks();

        assertEquals(manager.getSubTasks(), new ArrayList<Subtask>());
    }

    @Test
    void deleteEpics() {
        manager.createEpicTask(epic);

        manager.deleteEpics();

        assertEquals(manager.getEpics(), new ArrayList<Epic>());
    }

    @Test
    void getTaskByID() throws IntersectionTaskException {
        int taskId = manager.createTask(task1);
        assertEquals(manager.getTaskByID(taskId).get(), task1);
    }

    @Test
    void getSubTaskByID() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);
        subtask1.setEpicId(epicId);
        int subtaskId = manager.createSubTask(subtask1);
        assertEquals(manager.getSubTaskByID(subtaskId).get(), subtask1);
    }

    @Test
    void getEpicByID() {
        int epicId = manager.createEpicTask(epic);
        assertEquals(manager.getEpicByID(epicId).get(), epic);
    }

    @Test
    void createTask() throws IntersectionTaskException {
        manager.createTask(task1);
        assertEquals(manager.getTasks().getFirst(), task1);
    }

    @Test
    void createSubTask() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);
        subtask1.setEpicId(epicId);
        manager.createSubTask(subtask1);
        assertEquals(manager.getSubTasks().getFirst(), subtask1);
    }

    @Test
    void createEpicTask() {
        manager.createEpicTask(epic);
        assertEquals(manager.getEpics().getFirst(), epic);
    }

    @Test
    void updateTask() throws IntersectionTaskException {
        int taskId = manager.createTask(simpleTask);
        assertEquals(manager.getTaskByID(taskId).get(), simpleTask);
        Task managerTask = manager.getTaskByID(taskId).get();

        Task updatedTask = new Task(managerTask.getId(), managerTask.getName(),
                managerTask.getDescription(), managerTask.getStatus());
        updatedTask.setStatus(Status.IN_PROGRESS);

        manager.updateTask(updatedTask);
        assertNotEquals(manager.getTaskByID(taskId).get().getStatus(), simpleTask.getStatus());
    }

    @Test
    void updateSubTask() throws IntersectionTaskException {
        Subtask subtask = new Subtask(simpleTask.getName(), simpleTask.getDescription());
        subtask.setEpicId(manager.createEpicTask(epic));
        int taskId = manager.createSubTask(subtask);
        assertEquals(manager.getSubTaskByID(taskId).get(), subtask);
        Subtask managerTask = manager.getSubTaskByID(taskId).get();

        Subtask updatedTask = new Subtask(managerTask.getId(), managerTask.getName(),
                managerTask.getDescription(), managerTask.getStatus(), managerTask.getEpicId());
        updatedTask.setStatus(Status.IN_PROGRESS);

        manager.updateSubTask(updatedTask);
        assertNotEquals(manager.getSubTaskByID(taskId).get().getStatus(), simpleTask.getStatus());
    }

    @Test
    void updateEpicTask() {
        Epic epicTask = new Epic(simpleTask.getName(), simpleTask.getDescription());
        int taskId = manager.createEpicTask(epicTask);
        assertEquals(manager.getEpicByID(taskId).get(), epicTask);
        Epic managerEpic = manager.getEpicByID(taskId).get();

        Epic updatedEpic = new Epic(taskId, epicTask.getName(),
                epicTask.getDescription(), managerEpic.getStatus());
        updatedEpic.setName("Changed name");

        manager.updateEpicTask(updatedEpic);
        assertNotEquals(manager.getEpicByID(taskId).get().getName(), managerEpic.getName());
    }


    @Test
    @DisplayName("All epic subtasks are NEW.")
    void allEpicSubtasksNEW() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(Status.NEW, manager.getEpicByID(epicId).get().getStatus());
    }

    @Test
    @DisplayName("All epic subtasks are DONE.")
    void allEpicSubtasksDONE() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        subtask1.setStatus(Status.DONE);
        subtask2.setStatus(Status.DONE);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(Status.DONE, manager.getEpicByID(epicId).get().getStatus());
    }

    @Test
    @DisplayName("All epic subtasks are NEW and DONE.")
    void allEpicSubtasksNEWandDONE() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        subtask1.setStatus(Status.NEW);
        subtask2.setStatus(Status.DONE);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(Status.NEW, manager.getEpicByID(epicId).get().getStatus());
    }

    @Test
    @DisplayName("All epic subtasks are IN_PROGRESS")
    void allEpicSubtasksINPROGRESS() throws IntersectionTaskException {
        int epicId = manager.createEpicTask(epic);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        subtask1.setStatus(Status.IN_PROGRESS);
        subtask2.setStatus(Status.IN_PROGRESS);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicByID(epicId).get().getStatus());
    }

    @Test
    @DisplayName("Check intersection when it exists")
    void shouldBeFalseForIntersection() {
        Task intersectTask1 = new Task("Task 1", "Description 1",
                task1.getDuration().minusMinutes(10), task1.getStartTime());

        assertFalse(TasksIntersectionValidator.isValid(task1, intersectTask1));
    }

    @Test
    @DisplayName("Check intersection when it doesn't exist")
    void shouldBeTrueForIntersection() {
        assertTrue(TasksIntersectionValidator.isValid(task1, task2));
    }
}