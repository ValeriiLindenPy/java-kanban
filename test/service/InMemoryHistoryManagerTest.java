package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.interfaces.HistoryManager;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setInitState() {
        historyManager = Managers.getDefaultHistory();
        task1 = new Task(1, "Task 1", "Description 1", Status.NEW);
        task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS);
        task3 = new Task(3, "Task 3", "Description 3", Status.DONE);
    }

    @Test
    void addTaskToHistory(){
        historyManager.add(new Task("Task 1", "Description for task 1"));

        assertNotNull(historyManager.getHistory());
        assertEquals(1, historyManager.getHistory().size());
    }

    /**
     * задачи, добавляемые в HistoryManager, сохраняют предыдущую версию задачи и её данных.
     */
    @Test
    void testPreviousVersionsRetainOldVersionTask() {
        Task testTask = new Task( "Test", "test");
        historyManager.add(testTask);

        testTask.setName("Changed task");
        testTask.setDescription("Changed description");
        testTask.setStatus(Status.IN_PROGRESS);

        Task historyTask = historyManager.getHistory().getFirst();

        assertNotEquals(historyTask.getName(), testTask.getName());
        assertNotEquals(historyTask.getDescription(), testTask.getDescription());
        assertNotEquals(historyTask.getStatus(), testTask.getStatus());
    }



    @Test
    void testHistoryAddNewTaskToEnd() {
        Task testTask = new Task( "Test", "test");

        for (int i = 1; i < 11; i++) {
            testTask.setId(i);
            historyManager.add(testTask);
        }

        Task newTask = new Task("New Task", "new task");
        newTask.setId(12);
        historyManager.add(newTask);

        assertEquals(historyManager.getHistory().getLast(), newTask);
    }

    @Test
    void addAndGetHistoryTest() {
        // Adding tasks
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        // Getting history
        List<Task> history = historyManager.getHistory();

        assertEquals(3, history.size(), "History should contain 3 tasks");
        assertEquals(task1, history.get(0), "First task should be task1");
        assertEquals(task2, history.get(1), "Second task should be task2");
        assertEquals(task3, history.get(2), "Third task should be task3");
    }

    @Test
    void removeTaskTest() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks after removal");
        assertEquals(task1, history.get(0), "First task should be task1");
        assertEquals(task3, history.get(1), "Second task should be task3");
    }

    @Test
    void addDuplicateTaskTest() {
        historyManager.add(task1);
        historyManager.add(task2);

        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertEquals(task2, history.get(0), "First task should be task2");
        assertEquals(task1, history.get(1), "Second task should be the re-added task1");
    }

    @Test
    void removeFirstAndLastTaskTest() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "History should contain 1 task");
        assertEquals(task2, history.getFirst(), "Remaining task should be task2");
    }

    @Test
    void removeLastTaskTest() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task3.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertEquals(task1, history.getFirst(), "First remaining task should be task1");
        assertEquals(task2, history.get(1), "Second remaining task should be task2");
    }

    @Test
    void removeFirstTaskTest() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertEquals(task2, history.getFirst(), "First remaining task should be task2");
        assertEquals(task3, history.get(1), "Second remaining task should be task3");
    }

    @Test
    void removeMiddleTaskTest() {

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(task2.getId());

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain 2 tasks");
        assertEquals(task1, history.getFirst(), "First remaining task should be task1");
        assertEquals(task3, history.get(1), "Second remaining task should be task3");
    }

    @Test
    void removeTheOnlyOneTaskTest() {

        historyManager.add(task1);

        historyManager.remove(task1.getId());

        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void removeFromEmptyHistoryTest() {
        assertTrue(historyManager.getHistory().isEmpty(), "History should be empty initially");

        historyManager.remove(1);

        assertTrue(historyManager.getHistory().isEmpty(), "History should still be empty after attempting to remove from empty history");
    }



}