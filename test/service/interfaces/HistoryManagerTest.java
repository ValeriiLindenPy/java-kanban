package service.interfaces;

import model.enums.Status;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class HistoryManagerTest<T extends HistoryManager>  {
    protected T historyManager;
    protected Task task1;
    protected Task task2;
    protected Task task3;

    protected abstract T createHistoryManager();

    @BeforeEach
    void setInitState() {
        historyManager = createHistoryManager();
        LocalDateTime startTime =
                LocalDateTime.of(2024, 6, 11, 14, 9);
        Duration duration = Duration.ofMinutes(20);
        task1 = new Task(1, "Task 1", "Description 1", Status.NEW, startTime,duration);
        task2 = new Task(2, "Task 2", "Description 2", Status.IN_PROGRESS, startTime.plusHours(1), duration);
        task3 = new Task(3, "Task 3", "Description 3", Status.DONE, startTime.plusHours(2), duration);
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
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

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