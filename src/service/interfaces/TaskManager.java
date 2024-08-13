package service.interfaces;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {

    ArrayList<Task> getTasks();

    ArrayList<Subtask> getSubTasks();

    ArrayList<Epic> getEpics();

    void deleteTasks();

    void deleteSubTasks();

    void deleteEpics();

    Task getTaskByID(int id);

    Subtask getSubTaskByID(int id);

    Epic getEpicByID(int id);

    int createTask(Task task);

    int createSubTask(Subtask task);

    int createEpicTask(Epic task);

    void updateTask(Task task);

    void updateSubTask(Subtask task);

    void updateEpicTask(Epic task);

    void deleteTaskByID(int id);

    void deleteSubTaskByID(int id);

    void deleteEpicByID(int id);

    ArrayList<Subtask> getEpicSubTasks(int epicID);

    List<Task> getHistory();
}
