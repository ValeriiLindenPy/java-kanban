package api.hendlers.tasks;

import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Task;

import java.io.IOException;
import java.util.Optional;

public class TaskByIdHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetByIdTask(exchange);
            case "DELETE" -> handleDeleteByIdTask(exchange);
        }
    }

    private void handleDeleteByIdTask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");
        int taskId;

        if (uriPattern.length != 3) {
            sendCode(exchange, 404);
        }

        try {
            taskId = Integer.parseInt(uriPattern[2]);
            Optional<Task> taskOpt = manager.getTaskByID(taskId);

            if (taskOpt.isPresent()) {
                manager.deleteTaskByID(taskId);
                sendCode(exchange, 200);
            } else {
                sendCode(exchange, 404);
            }
        } catch (NumberFormatException nfe) {
            sendCode(exchange, 404);
        }
    }

    private void handleGetByIdTask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");
        int taskId;

        if (uriPattern.length != 3) {
            sendCode(exchange, 404);
        }

        try {
            taskId = Integer.parseInt(uriPattern[2]);
            Optional<Task> taskOpt = manager.getTaskByID(taskId);

            if (taskOpt.isPresent()) {
                Task task = taskOpt.get();
                String jsonTask = gson.toJson(task);
                sendText(exchange, jsonTask);
            } else {
                sendCode(exchange, 404);
            }
        } catch (NumberFormatException nfe) {
            sendCode(exchange, 404);
        }
    }
}
