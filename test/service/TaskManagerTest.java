package service;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TaskManagerTest {
    TaskManager manager;
    ArrayList<Task> tasksList;
    ArrayList<Subtask> subTasksList;
    ArrayList<Epic> epicsTasksList;


    @BeforeAll
    void setup() {
        manager = new TaskManager();
        tasksList = new ArrayList<>(Arrays.asList(
                new Task("Buy soup", "buy soup in shop"),
                new Task("Make dinner", "Make dinner for mom"),
                new Task("Make tea", "Make black tea")
        ));
        epicsTasksList = new ArrayList<>(Arrays.asList(
                //epic 1
                new Epic("Make home work", "Make home work for today"),
                //epic 2
                new Epic("Meet friends", "Meet all my friends")
        ));

        subTasksList = new ArrayList<>(Arrays.asList(
                //for epic 1
                new Subtask("Make algebra", "make task 1"),
                new Subtask("Make math", "make equations"),
                //for epic 2
                new Subtask("Meet Jack", "Meet Jack at 11"),
                new Subtask("Meet Sam", "Meet Sam at 12")
        ));

    }

    @BeforeEach
    void setInitState() {
        manager.deleteTasks();
        manager.deleteSubTasks();
        manager.deleteEpics();
    }

    @Test
    void getTasks() {
        assertTrue(manager.getTasks().isEmpty());
        for (Task task : tasksList) {
            manager.createTask(task);
        }

        assertEquals(tasksList.size(), manager.getTasks().size());
        assertFalse(manager.getTasks().isEmpty());

    }

    @Test
    void getSubTasks() {
        assertTrue(manager.getSubTasks().isEmpty());
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(subTasksList.size()-2, manager.getSubTasks().size());
        assertFalse(manager.getSubTasks().isEmpty());
    }

    @Test
    void getEpics() {
        assertTrue(manager.getEpics().isEmpty());
        manager.createEpicTask(epicsTasksList.getFirst());
        manager.createEpicTask(epicsTasksList.get(1));
        assertFalse(manager.getEpics().isEmpty());
        assertEquals(2, manager.getEpics().size());
    }

    @Test
    void deleteTasks() {
        assertTrue(manager.getTasks().isEmpty());
        manager.createTask(tasksList.getFirst());
        manager.createTask(tasksList.get(1));
        assertFalse(manager.getTasks().isEmpty());
        manager.deleteTasks();
        assertTrue(manager.getTasks().isEmpty());
    }

    @Test
    void deleteSubTasks() {
        assertTrue(manager.getSubTasks().isEmpty());
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);
        assertFalse(manager.getSubTasks().isEmpty());
        manager.deleteSubTasks();
        assertTrue(manager.getSubTasks().isEmpty());
        manager.deleteEpics();
    }

    @Test
    void deleteEpics() {
        assertTrue(manager.getEpics().isEmpty());
        manager.createEpicTask(epicsTasksList.getFirst());
        manager.createEpicTask(epicsTasksList.get(1));
        assertFalse(manager.getEpics().isEmpty());
        manager.deleteEpics();
        assertTrue(manager.getEpics().isEmpty());
    }

    @Test
    void getTaskByID() {
        int taskOneId = manager.createTask(tasksList.getFirst());
        int taskTwoId = manager.createTask(tasksList.get(1));

        assertEquals(tasksList.getFirst(), manager.getTaskByID(taskOneId));
        assertEquals(tasksList.get(1), manager.getTaskByID(taskTwoId));


    }

    @Test
    void getSubTaskByID() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        int taskOneId = manager.createSubTask(subtask1);
        int taskTwoId = manager.createSubTask(subtask2);

        assertEquals(subTasksList.getFirst(), manager.getSubTaskByID(taskOneId));
        assertEquals(subTasksList.get(1), manager.getSubTaskByID(taskTwoId));
    }

    @Test
    void getEpicByID() {
        int taskOneId = manager.createEpicTask(epicsTasksList.getFirst());
        int taskTwoId = manager.createEpicTask(epicsTasksList.get(1));

        assertEquals(epicsTasksList.getFirst(), manager.getEpicByID(taskOneId));
        assertEquals(epicsTasksList.get(1), manager.getEpicByID(taskTwoId));
    }

    @Test
    void createTask() {
        assertThrows(java.lang.NullPointerException.class, () -> manager.createTask(null));

        int taskOneId = manager.createTask(tasksList.getFirst());
        int taskTwoId = manager.createTask(tasksList.getFirst());

        assertNotEquals(taskOneId, taskTwoId);
        assertEquals(2, manager.getTasks().size());

    }

    @Test
    void createSubTask() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        int taskOneId = manager.createSubTask(subtask1);
        int taskTwoId = manager.createSubTask(subtask2);

        assertNotEquals(taskOneId, taskTwoId);
        assertEquals(2, manager.getSubTasks().size());
    }

    @Test
    void createEpicTask() {
        int taskOneId = manager.createEpicTask(epicsTasksList.getFirst());
        int taskTwoId = manager.createEpicTask(epicsTasksList.get(1));

        assertNotEquals(taskOneId, taskTwoId);
        assertEquals(2, manager.getEpics().size());
    }

    @Test
    void updateTask() {
        int taskOneId = manager.createTask(tasksList.getFirst());
        assertEquals(tasksList.getFirst(), manager.getTaskByID(taskOneId));

        Task updatedTask = tasksList.get(1);
        updatedTask.setId(taskOneId);
        updatedTask.setStatus(Status.IN_PROGRESS);

        manager.updateTask(updatedTask);
        assertNotEquals(tasksList.getFirst(), manager.getTaskByID(taskOneId));
        assertEquals(updatedTask, manager.getTaskByID(taskOneId));
    }

    @Test
    void updateSubTask() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask = subTasksList.getFirst();
        subtask.setEpicId(epicId);
        int taskOneId = manager.createSubTask(subtask);
        assertEquals(subTasksList.getFirst(), manager.getSubTaskByID(taskOneId));

        Subtask updatedSubTask = subTasksList.get(1);
        updatedSubTask.setEpicId(epicId);
        updatedSubTask.setId(taskOneId);
        updatedSubTask.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(updatedSubTask);
        assertNotEquals(subTasksList.getFirst(), manager.getSubTaskByID(taskOneId));
        assertEquals(updatedSubTask, manager.getSubTaskByID(taskOneId));
    }

    @Test
    void updateEpicTask() {
        int taskOneId = manager.createEpicTask(epicsTasksList.getFirst());
        assertEquals(epicsTasksList.getFirst(), manager.getEpicByID(taskOneId));

        Epic updatedTask = epicsTasksList.get(1);
        updatedTask.setId(taskOneId);
        updatedTask.setStatus(Status.IN_PROGRESS);

        manager.updateEpicTask(updatedTask);
        assertNotEquals(epicsTasksList.getFirst(), manager.getEpicByID(taskOneId));
        assertEquals(updatedTask, manager.getEpicByID(taskOneId));
    }

    @Test
    void deleteTasksByID() {
        int taskOneId = manager.createTask(tasksList.getFirst());
        assertNotNull(manager.getTaskByID(taskOneId));
        manager.deleteTaskByID(taskOneId);
        assertNull(manager.getTaskByID(taskOneId));
    }

    @Test
    void deleteSubTasksByID() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask = subTasksList.getFirst();
        subtask.setEpicId(epicId);
        int taskId = manager.createSubTask(subtask);

        assertNotNull(manager.getSubTaskByID(taskId));
        manager.deleteSubTaskByID(taskId);
        assertNull(manager.getSubTaskByID(taskId));


    }

    @Test
    void deleteEpicsByID() {
        int taskOneId = manager.createEpicTask(epicsTasksList.getFirst());
        assertNotNull(manager.getEpicByID(taskOneId));
        manager.deleteEpicByID(taskOneId);
        assertNull(manager.getEpicByID(taskOneId));
    }

    @Test
    void getEpicSubTasks() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        ArrayList<Subtask> testList = new ArrayList<>();
        testList.add(subTasksList.getFirst());
        testList.add(subTasksList.get(1));

        assertEquals(testList, manager.getEpicSubTasks(epicId));
        assertEquals(2, manager.getEpicSubTasks(epicId).size());

    }

    @Test
    @DisplayName("Test epic status changes depending on subtasks.")
    void getEpicStatusTest() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask1 = subTasksList.getFirst();
        Subtask subtask2 = subTasksList.get(1);

        subtask1.setEpicId(epicId);
        subtask2.setEpicId(epicId);

        manager.createSubTask(subtask1);
        manager.createSubTask(subtask2);

        assertEquals(Status.NEW, manager.getEpicByID(epicId).getStatus());

        subtask2.setStatus(Status.IN_PROGRESS);
        manager.updateSubTask(subtask2);
        assertNotEquals(Status.NEW, manager.getEpicByID(epicId).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicByID(epicId).getStatus());

        subtask1.setStatus(Status.DONE);
        manager.updateSubTask(subtask1);
        assertNotEquals(Status.NEW, manager.getEpicByID(epicId).getStatus());
        assertNotEquals(Status.DONE, manager.getEpicByID(epicId).getStatus());
        assertEquals(Status.IN_PROGRESS, manager.getEpicByID(epicId).getStatus());

        subtask2.setStatus(Status.DONE);
        manager.updateSubTask(subtask2);
        assertNotEquals(Status.IN_PROGRESS, manager.getEpicByID(epicId).getStatus());
        assertNotEquals(Status.NEW, manager.getEpicByID(epicId).getStatus());
        assertEquals(Status.DONE, manager.getEpicByID(epicId).getStatus());
    }
}