package api.urls;

import api.hendlers.HistoryHandler;
import api.hendlers.PrioritizedHandler;
import api.hendlers.epics.EpicByIdHandler;
import api.hendlers.epics.EpicHandler;
import api.hendlers.subtasks.SubtaskByIdHandler;
import api.hendlers.subtasks.SubtaskHandler;
import api.hendlers.tasks.TaskByIdHandler;
import api.hendlers.tasks.TaskHandler;
import com.sun.net.httpserver.HttpHandler;

import java.util.HashMap;
import java.util.Map;

public class Routs {
    private static Map<String, HttpHandler> routs = new HashMap<>(Map.of(
            "/tasks", new TaskHandler(),
            "/tasks/", new TaskByIdHandler(),
            "/subtasks", new SubtaskHandler(),
            "/subtasks/", new SubtaskByIdHandler(),
            "/epics", new EpicHandler(),
            "/epics/", new EpicByIdHandler(),
            "/prioritized", new PrioritizedHandler(),
            "/history", new HistoryHandler()
    ));

    public static Map<String, HttpHandler> getRouts() {
        return routs;
    }
}
