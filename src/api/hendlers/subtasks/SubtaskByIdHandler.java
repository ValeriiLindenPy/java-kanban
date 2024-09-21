package api.hendlers.subtasks;

import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;

import java.io.IOException;
import java.util.Optional;

public class SubtaskByIdHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetByIdSubtask(exchange);
            case "DELETE" -> handleDeleteByIdSubtask(exchange);
        }
    }

    private void handleDeleteByIdSubtask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");
        int taskId;

        if (uriPattern.length != 3) {
            sendCode(exchange, 404);
        }

        try {
            taskId = Integer.parseInt(uriPattern[2]);
            Optional<Subtask> taskOpt = manager.getSubTaskByID(taskId);

            if (taskOpt.isPresent()) {
                manager.deleteSubTaskByID(taskId);
                sendCode(exchange, 200);
            } else {
                sendCode(exchange, 404);
            }
        } catch (NumberFormatException nfe) {
            sendCode(exchange, 404);
        }
    }

    private void handleGetByIdSubtask(HttpExchange exchange) throws IOException {
        String uri = exchange.getRequestURI().getPath();
        String[] uriPattern = uri.split("/");
        int taskId;

        if (uriPattern.length != 3) {
            sendCode(exchange, 404);
        }

        try {
            taskId = Integer.parseInt(uriPattern[2]);
            Optional<Subtask> taskOpt = manager.getSubTaskByID(taskId);

            if (taskOpt.isPresent()) {
                Subtask task = taskOpt.get();
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
