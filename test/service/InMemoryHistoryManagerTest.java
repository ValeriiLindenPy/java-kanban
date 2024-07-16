package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryHistoryManagerTest {
    InMemoryTaskManager manager;
    ArrayList<Task> tasksList;
    ArrayList<Subtask> subTasksList;
    ArrayList<Epic> epicsTasksList;
    HistoryManager historyManager;

    @BeforeAll
    void setup() {
        historyManager = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager();
        tasksList = new ArrayList<>(Arrays.asList(
                new Task("Task 1", "Description for task 1"),
                new Task("Task 2", "Description for task 2"),
                new Task("Task 3", "Description for task 3"),
                new Task("Task 4", "Description for task 4"),
                new Task("Task 5", "Description for task 5"),
                new Task("Task 6", "Description for task 6"),
                new Task("Task 7", "Description for task 7"),
                new Task("Task 8", "Description for task 8"),
                new Task("Task 9", "Description for task 9"),
                new Task("Task 10", "Description for task 10")
        ));
        epicsTasksList = new ArrayList<>(Arrays.asList(
                //epic 1
                new Epic("Epic 1", "Description for epic 1"),
                //epic 2
                new Epic("Epic 2", "Description for epic 2")
        ));

        subTasksList = new ArrayList<>(Arrays.asList(
                //for epic 1
                new Subtask("Subtask 1", "Description for subtask 1"),
                new Subtask("Subtask 2", "Description for subtask 2"),
                new Subtask("Subtask 3", "Description for subtask 3"),
                new Subtask("Subtask 4", "Description for subtask 4")
        ));
    }

    @BeforeEach
    void setInitState() {
        manager.resetHistory();
        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();
        manager.resetTaskCounter();
    }

    @Test
    void addTaskToHistory(){
        manager.createTask(tasksList.getFirst());
        manager.getTaskByID(1);

        assertNotNull(manager.getHistory(), "История не пустая.");
        assertEquals(1, manager.getHistory().size(), "История не пустая.");
    }

    /**
     * задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
     */

    @Test
    void testPreviousVersionsRetainOldVersionTask() {
        Task testTask = new Task( "Test", "test");
        int testTaskId = manager.createTask(testTask);
        manager.getTaskByID(testTaskId);

        //change original task
        testTask.setName("Changed task");
        testTask.setDescription("Changed description");
        testTask.setStatus(Status.IN_PROGRESS);

        //retrieve original testTask from history
        Task historyTask = manager.getHistory().getFirst();

        // compare original testTask in history and current testTask
        assertNotEquals(historyTask.getName(), testTask.getName());
        assertNotEquals(historyTask.getDescription(), testTask.getDescription());
        assertNotEquals(historyTask.getStatus(), testTask.getStatus());
    }

    @Test
    void TestMaxTasksInHistory() {
        int currentId = 1;
        List<Task> historyTasksList;

        for (Task task : tasksList) {
            task.setId(currentId);
            historyManager.add(task);
            currentId++;
        }

        historyTasksList = new ArrayList<>(historyManager.getHistory());

        assertEquals(10, historyTasksList.size());
        Task newTask = new Task("New Task", "new task");
        newTask.setId(12);
        historyManager.add(newTask);

        //Test MAX_TASKS_IN_HISTORY
        assertEquals(10, historyTasksList.size());
    }

    @Test
    void testHistoryAddNewTaskToEnd() {
        int currentId = 1;
        List<Task> historyTasksList;

        for (Task task : tasksList) {
            task.setId(currentId);
            historyManager.add(task);
            currentId++;
        }

        Task newTask = new Task("New Task", "new task");
        newTask.setId(12);
        historyManager.add(newTask);
        historyTasksList = new ArrayList<>(historyManager.getHistory());

        assertEquals(historyTasksList.getLast(), newTask);
    }



}