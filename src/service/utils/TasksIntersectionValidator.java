package service.utils;

import model.Task;

public class TasksIntersectionValidator {

    public static boolean isValid(Task taskFromList, Task newTask) {
        return taskFromList.getStartTime().isAfter(newTask.getEndTime()) ||
                taskFromList.getEndTime().isBefore(newTask.getStartTime());
    }
}
