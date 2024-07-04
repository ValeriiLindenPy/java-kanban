package service;

import model.EpicTask;
import model.SubTask;
import model.Task;

import java.util.ArrayList;


public interface ITaskmanager {
    //Получение списка всех задач.
    ArrayList<Task> getTasks();
    ArrayList<SubTask> getSubTasks();
    ArrayList<EpicTask> getEpics();

    //Удаление всех задач.
    void deleteTasks();
    void deleteSubTasks();
    void deleteEpics();

    //Получение по идентификатору.
    Task getTaskByID(int id);
    SubTask getSubTaskByID(int id);
    EpicTask getEpicByID(int id);

    //Создание.
    int createTask(Task task);
    int createSubTask(SubTask task);
    int createEpicTask(EpicTask task);

    //Обновление
    void updateTask(Task task);
    void updateSubTask(SubTask task);
    void updateEpicTask(EpicTask task);

    //Удаление задач по ID.
    void deleteTaskByID(int id);
    void deleteSubTaskByID(int id);
    void deleteEpicByID(int id);

    //Получение списка всех подзадач определённого эпика
    ArrayList<SubTask> getEpicSubTasks(int epicID);



}
