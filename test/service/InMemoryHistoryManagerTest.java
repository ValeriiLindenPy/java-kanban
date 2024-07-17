package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;


import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InMemoryHistoryManagerTest {
    HistoryManager historyManager;


    @BeforeEach
    void setInitState() {
        historyManager = Managers.getDefaultHistory();
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

        //change original task
        testTask.setName("Changed task");
        testTask.setDescription("Changed description");
        testTask.setStatus(Status.IN_PROGRESS);

        //retrieve original testTask from history
        Task historyTask = historyManager.getHistory().getFirst();

        // compare original testTask in history and current testTask
        assertNotEquals(historyTask.getName(), testTask.getName());
        assertNotEquals(historyTask.getDescription(), testTask.getDescription());
        assertNotEquals(historyTask.getStatus(), testTask.getStatus());
    }

    @Test
    void TestMaxTasksInHistory() {

        Task testTask = new Task( "Test", "test");

        for (int i = 0; i < 11; i++) {
            historyManager.add(testTask);
        }

        assertEquals(10, historyManager.getHistory().size());
        Task newTask = new Task("New Task", "new task");
        newTask.setId(12);
        historyManager.add(newTask);

        //Test MAX_TASKS_IN_HISTORY
        assertEquals(10, historyManager.getHistory().size());
    }

    @Test
    void testHistoryAddNewTaskToEnd() {
        Task testTask = new Task( "Test", "test");

        for (int i = 0; i < 11; i++) {
            historyManager.add(testTask);
        }

        Task newTask = new Task("New Task", "new task");
        newTask.setId(12);
        historyManager.add(newTask);

        assertEquals(historyManager.getHistory().getLast(), newTask);
    }



}