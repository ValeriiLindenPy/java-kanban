package api.hendlers.subtasks;

import api.hendlers.BaseHttpHandler;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.IOException;
import java.util.Optional;

public class SubtaskHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");

        if (uriPattern.length == 3) {
            int subtaskId;
            try {
                subtaskId = Integer.parseInt(uriPattern[2]);

                switch (method) {
                    case "GET" -> handleGetByIdSubtask(exchange, subtaskId);
                    case "DELETE" -> handleDeleteByIdSubtask(exchange, subtaskId);
                    default -> sendCode(exchange, 405);
                }
            } catch (NumberFormatException e) {
                sendCode(exchange, 404);
            }
        } else if (uriPattern.length == 2) {
            switch (method) {
                case "GET" -> handleGetAllSubtasks(exchange);
                case "POST" -> handleCreateOrUpdateSubtask(exchange);
                default -> sendCode(exchange, 405);
            }
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleCreateOrUpdateSubtask(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String stringSubtask = new String(bytes);

        Subtask subtask;
        try {
            subtask = gson.fromJson(stringSubtask, Subtask.class);

            if (subtask.getId() == 0) {
                try {
                    manager.createSubTask(subtask);
                    sendCode(exchange, 201);
                } catch (IntersectionTaskException e) {
                    sendCode(exchange, 406);
                } catch (IllegalArgumentException e) {
                    sendText(exchange, "Error: " + e.getMessage());
                }
            } else {
                try {
                    manager.updateSubTask(subtask);
                    sendCode(exchange, 200);
                } catch (IntersectionTaskException e) {
                    sendCode(exchange, 406);
                }
            }
        } catch (JsonSyntaxException jse) {
            sendText(exchange, "Error: Invalid JSON format");
            sendCode(exchange, 400);
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        try {
            String jsonResponse = gson.toJson(manager.getSubTasks());
            sendText(exchange, jsonResponse);
        } catch (Exception e) {
            sendCode(exchange, 500);
        }
    }

    private void handleGetByIdSubtask(HttpExchange exchange, int subtaskId) throws IOException {
        Optional<Subtask> taskOpt = manager.getSubTaskByID(subtaskId);

        if (taskOpt.isPresent()) {
            String jsonTask = gson.toJson(taskOpt.get());
            sendText(exchange, jsonTask);
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleDeleteByIdSubtask(HttpExchange exchange, int subtaskId) throws IOException {
        Optional<Subtask> taskOpt = manager.getSubTaskByID(subtaskId);

        if (taskOpt.isPresent()) {
            manager.deleteSubTaskByID(subtaskId);
            sendCode(exchange, 200);
        } else {
            sendCode(exchange, 404);
        }
    }
}
