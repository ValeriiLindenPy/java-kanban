package service.utils;

import model.*;

public class CSVFileFormater {

    public static String toString(Task task) {
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        String type;

        if (task.getClass() == Subtask.class) {
            type = Type.SUBTASK.toString();
            String epic = Integer.toString(((Subtask) task).getEpicId());
            return String.join(",", id,type,name,status,description,epic);
        } else if (task.getClass() == Epic.class) {
            type = Type.EPIC.toString();
            return String.join(",", id,type,name,status,description) + ",";
        }
        type = Type.TASK.toString();
        return String.join(",", id,type,name,status,description) + ",";
    }

    public static Task fromString(String value) {
        String[] taskArray = value.split(",");
        Type type = parseType(value);

        return switch (type) {
            case SUBTASK-> new Subtask(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4],parseStatus(taskArray[3]), Integer.parseInt(taskArray[5]));
            case EPIC -> new Epic(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4],parseStatus(taskArray[3]));
            case TASK -> new Task(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4],parseStatus(taskArray[3]));
            default -> null;
        };
    }

    public static Type parseType(String value) {
        String[] taskArray = value.split(",");
        String type = taskArray[1];

        return switch (type) {
            case "SUBTASK" -> Type.SUBTASK;
            case "EPIC" -> Type.EPIC;
            case "TASK" -> Type.TASK;
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
