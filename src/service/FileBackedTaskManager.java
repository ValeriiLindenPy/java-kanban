package service;

import model.*;
import service.utils.CSVFileFormater;
import service.utils.ManagerSaveException;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File taskFile;

    public FileBackedTaskManager(File taskFile) {
        this.taskFile = taskFile;
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        final FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Skip the header
            List<String> epicLines = new ArrayList<>();
            List<String> subtaskLines = new ArrayList<>();

            while (reader.ready()) {
                String line = reader.readLine();
                if (line == null || line.isEmpty()) {
                    continue;
                }

                Type type = CSVFileFormater.parseType(line);

                switch (type) {
                    case EPIC -> epicLines.add(line);
                    case SUBTASK -> subtaskLines.add(line);
                    case TASK -> manager.createTask(CSVFileFormater.fromString(line));
                }
            }

            for (String epicLine : epicLines) {
                manager.createEpicTask((Epic) CSVFileFormater.fromString(epicLine));
            }

            for (String subtaskLine : subtaskLines) {
                manager.createSubTask((Subtask) CSVFileFormater.fromString(subtaskLine));
            }
        } catch (IOException e) {
            System.out.println("FileBacked error!");
        }

        return manager;
    }



    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.taskFile))) {

            if (taskFile.length() == 0) {
                writer.write("id,type,name,status,description,epic\n");
            }

            for (Task task : getTasks()) {
                writer.write(CSVFileFormater.toString(task));
                writer.newLine();
            }

            for (Subtask subTask : getSubTasks()) {
                writer.write(CSVFileFormater.toString(subTask));
                writer.newLine();
            }

            for (Epic epic : getEpics()) {
                writer.write(CSVFileFormater.toString(epic));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Error saving tasks to file: " + e.getMessage(), e);
        }

    }


    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteSubTasks() {
        super.deleteSubTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public int createTask(Task task) {
        int id = super.createTask(task);
        save();
        return id;

    }

    @Override
    public int createSubTask(Subtask task) {
        int id = super.createSubTask(task);
        save();
        return id;
    }

    @Override
    public int createEpicTask(Epic task) {
        int id = super.createEpicTask(task);
        save();
        return id;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateSubTask(Subtask task) {
        super.updateSubTask(task);
        save();
    }

    @Override
    public void updateEpicTask(Epic task) {
        super.updateEpicTask(task);
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteSubTaskByID(int id) {
        super.deleteSubTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }



    public static void main(String[] args) throws IOException {
        File file = new File("src/tasks.csv");

        FileBackedTaskManager fileManger = Managers.getFileBasedManager(file);
        Task task = new Task("test name 1", "test description 1");
        Task task2 = new Task("test name 2", "test description 2");
        fileManger.createTask(task);
        fileManger.createTask(task2);

        FileBackedTaskManager restoredFileManager = FileBackedTaskManager.loadFromFile(file);
        assert restoredFileManager.getTasks().equals(fileManger.getTasks());
    }

}
