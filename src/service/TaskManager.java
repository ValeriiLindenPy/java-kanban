package service;

import model.Epic;
import model.Subtask;
import model.Task;
import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    //Получение списка всех задач.
    ArrayList<Task> getTasks();
    ArrayList<Subtask> getSubTasks();
    ArrayList<Epic> getEpics();

    //Удаление всех задач.
    void deleteTasks();
    void deleteSubTasks();
    void deleteEpics();

    //Получение по идентификатору.
    Task getTaskByID(int id);
    Subtask getSubTaskByID(int id);
    Epic getEpicByID(int id);

    //Создание.
    int createTask(Task task);
    int createSubTask(Subtask task);
    int createEpicTask(Epic task);

    //Обновление
    void updateTask(Task task);
    void updateSubTask(Subtask task);
    void updateEpicTask(Epic task);

    //Удаление задач по ID.
    void deleteTaskByID(int id);
    void deleteSubTaskByID(int id);
    void deleteEpicByID(int id);

    //Получение списка всех подзадач определённого эпика
    ArrayList<Subtask> getEpicSubTasks(int epicID);

    // получить историю задач
    List<Task> getHistory();
}
