package api.hendlers.epics;

import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class EpicHandler extends BaseHttpHandler {

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
                    case "GET" -> handleGetByIdTask(exchange, taskId);
                    case "DELETE" -> handleDeleteByIdTask(exchange, taskId);
                    default -> sendCode(exchange, 405);
                }
            } catch (NumberFormatException e) {
                sendCode(exchange, 404);
            }
        } else if (uriPattern.length == 4 && uriPattern[3].equals("subtasks")) {
            int taskId;
            try {
                taskId = Integer.parseInt(uriPattern[2]);
                if ("GET".equals(method)) {
                    handleGetSubtasks(exchange, taskId);
                } else {
                    sendCode(exchange, 405);
                }
            } catch (NumberFormatException e) {
                sendCode(exchange, 404);
            }
        } else {
            switch (method) {
                case "GET" -> handleGetAllEpics(exchange);
                case "POST" -> handleCreateEpic(exchange);
                default -> sendCode(exchange, 405);
            }
        }
    }

    private void handleDeleteByIdTask(HttpExchange exchange, int taskId) throws IOException {
        Optional<Epic> taskOpt = manager.getEpicByID(taskId);

        if (taskOpt.isPresent()) {
            manager.deleteEpicByID(taskId);
            sendCode(exchange, 200);
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleGetByIdTask(HttpExchange exchange, int taskId) throws IOException {
        Optional<Epic> taskOpt = manager.getEpicByID(taskId);

        if (taskOpt.isPresent()) {
            Epic epic = taskOpt.get();
            String jsonTask = gson.toJson(epic);
            sendText(exchange, jsonTask);
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleGetSubtasks(HttpExchange exchange, int taskId) throws IOException {
        Optional<Epic> taskOpt = manager.getEpicByID(taskId);

        if (taskOpt.isPresent()) {
            List<Subtask> subtasks = manager.getEpicSubTasks(taskId);
            String jsonSubtasks = gson.toJson(subtasks);
            sendText(exchange, jsonSubtasks);
        } else {
            sendCode(exchange, 404);
        }
    }

    private void handleCreateEpic(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String stringTask = new String(bytes);

        Epic epic = gson.fromJson(stringTask, Epic.class);
        epic.setSubtasksIds(new HashSet<>());

        if (epic.getId() == 0) {
            manager.createEpicTask(epic);
            exchange.sendResponseHeaders(201, -1);
        } else {
            manager.updateEpicTask(epic);
            sendCode(exchange, 201);
        }
        exchange.close();
    }

    private void handleGetAllEpics(HttpExchange exchange) throws IOException {
        try {
            String jsonResponse = gson.toJson(manager.getEpics());
            sendText(exchange, jsonResponse);
        } catch (Exception e) {
            sendCode(exchange, 500);
        }
    }
}
