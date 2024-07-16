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
class InMemoryTaskManagerTest {
    InMemoryTaskManager manager;
    ArrayList<Task> tasksList;
    ArrayList<Subtask> subTasksList;
    ArrayList<Epic> epicsTasksList;


    @BeforeAll
    void setup() {
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

    /**
     * экземпляры класса Task равны друг другу, если равен их id;
     */

    @Test
    void shouldBeEqualWhenTwoTasksHaveOneId() {
        Task taskOne = new Task(1, "Task 1" , "task 1", Status.NEW);
        Task taskTwo = new Task(1, "Task 2" , "task 2", Status.NEW);

        assertEquals(taskOne, taskTwo);
    }

    /**
     * наследники класса Task равны друг другу, если равен их id;
     */

    @Test
    void shouldBeEqualWhenTaskSubclassesHaveOneId() {
        Epic epicOne = new Epic(1, "Epic 1" , "Epic 1", Status.NEW);
        Epic epicTwo = new Epic(1, "Epic 2" , "Epic 2", Status.NEW);

        Subtask subtaskOne = new Subtask(1, "Subtask 1" , "subtask 1", Status.NEW);
        Subtask subtaskTwo = new Subtask(1, "Subtask 2" , "subtask 2", Status.NEW);

        assertEquals(epicOne, epicTwo);
        assertEquals(subtaskOne, subtaskTwo);
    }

    /**
     * объект Epic нельзя добавить в самого себя в виде подзадачи;
     */

    @Test
    public void testEpicCannotAddItselfAsSubtask() {
        Epic epic = new Epic("Epic Task", "Description of Epic Task");
        int epicId = manager.createEpicTask(epic);
        epic.setId(epicId);

        // Attempt to add the epic as a subtask to itself
        assertThrows(IllegalArgumentException.class, () -> epic.addSubTask(epicId));

    }

    /**
     * объект Subtask нельзя сделать своим же эпиком;
     */

    @Test
    public void testSubtaskCannotAddItselfAsEpic() {
        int testId = 1;
        Subtask testSubtask = new Subtask(testId, "Test", "test", Status.NEW);

        assertThrows(IllegalArgumentException.class, () -> testSubtask.setEpicId(testId));

    }

    /**
     * задачи с заданным id и сгенерированным id не конфликтуют внутри менеджера;
     */

    @Test
    void shouldNotConflictWithSpecifiedIdAndGeneratedId() {
        int specifiedId = 1;
        Task specifiedIdTask = new Task("Task with ID", "Task description");
        specifiedIdTask.setId(specifiedId);
        manager.createTask(specifiedIdTask);


        Task generatedIdTask = new Task("Auto-generated ID Task", "Task description");
        int generatedId = manager.createTask(generatedIdTask);

        assertNotEquals(specifiedIdTask.getId(), generatedId);
    }

    /**
     * проверяется неизменность задачи (по всем полям) при добавлении задачи в менеджер
     */

    @Test
    void shouldMaintainTaskImmutabilityWhenAddedToManager() {
        Task originalTask = new Task(1, "Test Task", "Test Description", Status.NEW);

        manager.createTask(originalTask);

        Task retrievedTask = manager.getTaskByID(1);

        assertEquals(originalTask.getId(), retrievedTask.getId());
        assertEquals(originalTask.getName(), retrievedTask.getName());
        assertEquals(originalTask.getDescription(), retrievedTask.getDescription());
        assertEquals(originalTask.getStatus(), retrievedTask.getStatus());

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

    /**
     * InMemoryTaskManager действительно добавляет
     * задачи разного типа и может найти их по id;
     */

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
        assertNotEquals(tasksList.getFirst().getStatus(), manager.getTaskByID(taskOneId).getStatus());
        assertEquals(updatedTask, manager.getTaskByID(taskOneId));
    }

    @Test
    void updateSubTask() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());
        Subtask subtask = subTasksList.getFirst();
        subtask.setEpicId(epicId);
        int taskOneId = manager.createSubTask(subtask);
        assertEquals(subTasksList.getFirst(), manager.getSubTaskByID(taskOneId));

        Subtask updatedSubTask = new Subtask("Test task", "test subtask");
        updatedSubTask.setEpicId(epicId);
        updatedSubTask.setId(taskOneId);

        manager.updateSubTask(updatedSubTask);
        assertNotEquals(subTasksList.getFirst().getName(), manager.getSubTaskByID(taskOneId).getName());
        assertEquals(updatedSubTask, manager.getSubTaskByID(taskOneId));
    }

    @Test
    void updateEpicTask() {
        int epicId = manager.createEpicTask(epicsTasksList.getFirst());

        Epic updatedEpic = new Epic("Epic test", "epic test");
        updatedEpic.setName("New name");
        updatedEpic.setId(epicId);

        manager.updateEpicTask(updatedEpic);

        assertNotEquals(epicsTasksList.getFirst().getName(), manager.getEpicByID(epicId).getName());

        assertEquals(updatedEpic.getName(), manager.getEpicByID(epicId).getName());
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