package api.hendlers.tasks;

import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.IOException;
import java.util.Optional;

public class TaskHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");

        if (uriPattern.length == 3) {
            int taskId;
            try {
                taskId = Integer.parseInt(uriPattern[2]);

                switch (method) {
                    case "GET" -> handleGetTaskById(exchange, taskId);
                    case "DELETE" -> handleDeleteTaskById(exchange, taskId);
                }
            } catch (NumberFormatException e) {
                sendCode(exchange, 404);
            }
        } else {
            switch (method) {
                case "GET" -> handleGetAllTasks(exchange);
                case "POST" -> handleCreateOrUpdateTask(exchange);
            }
        }
    }

    private void handleCreateOrUpdateTask(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String stringTask = new String(bytes);
        Task task = gson.fromJson(stringTask, Task.class);

        if (task.getId() == 0) {
            try {
                manager.createTask(task);
                exchange.sendResponseHeaders(201, -1);
            } catch (IntersectionTaskException e) {
                sendCode(exchange, 406);
            }
        } else {
            try {
                manager.updateTask(task);
                sendCode(exchange, 201);
            } catch (IntersectionTaskException e) {
                sendCode(exchange, 406);
            }
        }
        exchange.close();
    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(manager.getTasks());
        sendText(exchange, jsonResponse);
    }

    private void handleGetTaskById(HttpExchange exchange, int taskId) throws IOException {
        Optional<Task> taskOpt = manager.getTaskByID(taskId);
        if (taskOpt.isPresent()) {
            Task task = taskOpt.get();
            String jsonTask = gson.toJson(task);
            sendText(exchange, jsonTask);
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleDeleteTaskById(HttpExchange exchange, int taskId) throws IOException {
        Optional<Task> taskOpt = manager.getTaskByID(taskId);
        if (taskOpt.isPresent()) {
            manager.deleteTaskByID(taskId);
            sendCode(exchange, 200);
        } else {
            sendCode(exchange, 404);
        }
    }
}
