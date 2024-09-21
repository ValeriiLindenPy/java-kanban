package service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import service.interfaces.TaskManagerTest;
import service.managersImpl.FileBackedTaskManager;
import service.utils.customExceptions.IntersectionTaskException;


import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return Managers.getFileBasedManager(file);
    }


    @Test
    void shouldRestoreAllTaskTypesFromFile() throws IOException, IntersectionTaskException {
        manager.createTask(task1);
        manager.createTask(task2);
        int epicId = manager.createEpicTask(epic);
        subtask2.setEpicId(epicId);
        manager.createSubTask(subtask2);

        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(manager.getTasks(), restoredManager.getTasks());
        assertEquals(manager.getEpics(), restoredManager.getEpics());
        assertEquals(manager.getSubTasks(), restoredManager.getSubTasks());
    }

    @Test
    void shouldSaveAndLoadEmptyFile() throws IOException {
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getSubTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
    }

    @Test
    void shouldRestoreAfterTaskDeletion() throws IOException, IntersectionTaskException {
        int task1Id = manager.createTask(task1);
        int task2Id = manager.createTask(task2);

        manager.deleteTaskByID(task1Id);

        FileBackedTaskManager restoredManager = FileBackedTaskManager.loadFromFile(file);

        assertEquals(restoredManager.getTaskByID(task1Id), Optional.empty());
        assertNotNull(restoredManager.getTaskByID(task2Id));
    }


}