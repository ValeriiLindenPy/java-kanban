package api.hendlers.subtasks;


import api.hendlers.BaseHttpHandler;
import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import model.Subtask;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.IOException;

public class SubtaskHandler extends BaseHttpHandler {


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetAllSubtasks(exchange);
            case "POST" -> handleCreateSubtask(exchange);
        }

    }

    private void handleCreateSubtask(HttpExchange exchange) throws IOException {

        byte[] bytes = exchange.getRequestBody().readAllBytes();

        String stringSubtask = new String(bytes);

        Subtask subtask;

        try {
            subtask = gson.fromJson(stringSubtask, Subtask.class);
            if (subtask.getId() == 0) {
                try {
                    try {
                        manager.createSubTask(subtask);
                    } catch (IllegalArgumentException iae) {
                        sendText(exchange, "Error: " + iae.getMessage());
                    }
                    exchange.sendResponseHeaders(201, -1);
                    exchange.close();
                } catch (IntersectionTaskException e) {
                    sendCode(exchange, 406);
                } catch (Exception e) {
                    String error = e.getMessage();
                    sendText(exchange, error);
                }
            } else {
                try {
                    manager.updateSubTask(subtask);
                    sendCode(exchange, 201);
                } catch (IntersectionTaskException e) {
                    sendCode(exchange, 406);
                }
            }
        } catch (JsonSyntaxException jse) {
            sendText(exchange, "Task problem");
        }
    }

    private void handleGetAllSubtasks(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(manager.getSubTasks());
        sendText(exchange, jsonResponse);
    }
}
