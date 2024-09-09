package service.utils;

import model.Task;

public class TasksIntersectionValidator {

    public static boolean isValid(Task task1, Task task2) {
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return task1.getEndTime().isBefore(task2.getStartTime());
        } else if (task2.getStartTime().isBefore(task1.getStartTime())) {
            return task2.getEndTime().isBefore(task1.getStartTime());
        }

        return false;
    }
}
