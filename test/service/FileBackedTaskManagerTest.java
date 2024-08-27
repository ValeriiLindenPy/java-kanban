package service;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileBackedTaskManagerTest {
    private FileBackedTaskManager manager;
    private File file;
    private Task task1;
    private Task task2;
    private Subtask subtask;
    private Epic epic;

    @BeforeEach
    void setInitState() throws IOException {
        file = File.createTempFile("temp", ".csv");
        manager = Managers.getFileBasedManager(file);
        task1 = new Task( "Task 1", "Description 1");
        task2 = new Task("Task 2", "Description 2");
        epic = new Epic("Epic 1", "Epic Description 1");
        subtask= new Subtask("Subtask 1", "Subtask Description 1");
    }

    @Test
    void loadFromFile() throws IOException {
        manager.createTask(task1);
        manager.createTask(task2);
        int epicId = manager.createEpicTask(epic);
        subtask.setEpicId(epicId);
        manager.createSubTask(subtask);

        FileBackedTaskManager restoredFileManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getTasks(), restoredFileManager.getTasks());
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
    }
}