package service.utils;

import model.*;

public class CSVFileFormater {

    public static String toString(Task task) {
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        String type = task.getType().toString(); // Use the new getType() method

        if (task instanceof Subtask subtask) {
            String epic = Integer.toString(subtask.getEpicId());
            return String.join(",", id, type, name, status, description, epic);
        } else if (task instanceof Epic) {
            return String.join(",", id, type, name, status, description) + ",";
        }
        return String.join(",", id, type, name, status, description) + ",";
    }


    public static Task fromString(String value) {
        String[] taskArray = value.split(",");
        String type = taskArray[1];

        return switch (type) {
            case "SUBTASK" -> new Subtask(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]), Integer.parseInt(taskArray[5]));
            case "EPIC" -> new Epic(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]));
            case "TASK" -> new Task(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]));
            default -> null;
        };
    }

    private static Status parseStatus(String value) {
        return switch (value) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> null;
        };
    }
}
