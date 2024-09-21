package service.interfaces;

import model.Epic;
import model.Subtask;
import model.Task;
import service.utils.customExceptions.IntersectionTaskException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Epic> getEpics();

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    Optional<Task> getTaskByID(int id);

    Optional<Subtask> getSubTaskByID(int id);

    Optional<Epic> getEpicByID(int id);

    int createTask(Task task) throws IntersectionTaskException;

    int createSubTask(Subtask task) throws IntersectionTaskException;

    int createEpicTask(Epic task);

    void updateTask(Task task) throws IntersectionTaskException;

    void updateSubTask(Subtask task) throws IntersectionTaskException;

    void updateEpicTask(Epic task);

    void deleteTaskByID(int id);

    void deleteSubTaskByID(int id);

    void deleteEpicByID(int id);

    ArrayList<Subtask> getEpicSubTasks(int epicID);

    List<Task> getHistory();

    void clearHistory();

    List<Task> getPrioritizedTasks();

    void resetTaskCounter();
}
