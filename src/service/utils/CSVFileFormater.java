package service.utils;

import model.*;
import model.enums.Status;
import model.enums.Type;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CSVFileFormater {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yy - HH:mm");

    public static String toString(Task task) {
        String id = Integer.toString(task.getId());
        String name = task.getName();
        String status = task.getStatus().toString();
        String description = task.getDescription();
        String type = task.getType().toString();
        String startTime = "";
        if (task.getStartTime() != null) {
            startTime = task.getStartTime().format(DATE_TIME_FORMATTER);
        }

        String duration = "";

        if (task.getDuration() != null) {
            duration = String.valueOf(task.getDuration().toMinutes());
        }

        if (task instanceof Subtask subtask) {
            String epic = Integer.toString(subtask.getEpicId());
            return String.join(",", id, type, name, status, description, startTime, duration, epic);
        } else if (task instanceof Epic) {
            return String.join(",", id, type, name, status, description, startTime, duration) + ",";
        }
        return String.join(",", id, type, name, status, description, startTime, duration) + ",";
    }


    public static Task fromString(String value) {
        String[] taskArray = value.split(",");
        Type type = Type.valueOf(taskArray[1]);

        return switch (type) {
            case SUBTASK -> new Subtask(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]), LocalDateTime.parse(taskArray[5], DATE_TIME_FORMATTER),
                    Duration.ofMinutes(Long.parseLong(taskArray[6])), Integer.parseInt(taskArray[7]));
            case EPIC -> new Epic(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]), LocalDateTime.parse(taskArray[5], DATE_TIME_FORMATTER),
                    Duration.ofMinutes(Long.parseLong(taskArray[6])));
            case TASK -> new Task(Integer.parseInt(taskArray[0]), taskArray[2],
                    taskArray[4], parseStatus(taskArray[3]), LocalDateTime.parse(taskArray[5], DATE_TIME_FORMATTER),
                    Duration.ofMinutes(Long.parseLong(taskArray[6])));
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
