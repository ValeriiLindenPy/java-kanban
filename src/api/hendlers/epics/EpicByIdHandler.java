package api.hendlers.epics;

import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EpicByIdHandler extends BaseHttpHandler {

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
            Optional<Epic> taskOpt = manager.getEpicByID(taskId);

            if (taskOpt.isPresent()) {
                manager.deleteEpicByID(taskId);
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

        if (uriPattern.length < 3) {
            sendCode(exchange, 404);
        } else if (uriPattern.length == 3) {
            try {
                taskId = Integer.parseInt(uriPattern[2]);
                Optional<Epic> taskOpt = manager.getEpicByID(taskId);

                if (taskOpt.isPresent()) {
                    Epic epic = taskOpt.get();
                    String jsonTask = gson.toJson(epic);
                    sendText(exchange, jsonTask);
                } else {
                    sendCode(exchange, 404);
                }
            } catch (NumberFormatException nfe) {
                sendCode(exchange, 404);
            }
        } else if (uriPattern.length == 4 &&
                Arrays.stream(uriPattern).collect(Collectors.toList()).getLast().equals("subtasks")) {
            try {
                taskId = Integer.parseInt(uriPattern[2]);
                Optional<Epic> taskOpt = manager.getEpicByID(taskId);

                if (taskOpt.isPresent()) {
                    List<Subtask> subtasks = manager.getEpicSubTasks(taskId);
                    String jsonTask = gson.toJson(subtasks);
                    sendText(exchange, jsonTask);
                } else {
                    sendCode(exchange, 404);
                }
            } catch (NumberFormatException nfe) {
                sendCode(exchange, 404);
            }
        } else {
            sendCode(exchange, 404);
        }
    }
}
