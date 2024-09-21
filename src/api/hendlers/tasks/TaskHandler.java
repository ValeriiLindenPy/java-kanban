package api.hendlers.tasks;


import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetAllTasks(exchange);
            case "POST" -> handleCreateTask(exchange);
        }

    }

    private void handleCreateTask(HttpExchange exchange) throws IOException {
        byte[] bytes = exchange.getRequestBody().readAllBytes();
        String stringTask = new String(bytes);

        Task task = gson.fromJson(stringTask, Task.class);

        if (task.getId() == 0) {
            try {
                manager.createTask(task);
                exchange.sendResponseHeaders(201, -1);
                exchange.close();
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

    }

    private void handleGetAllTasks(HttpExchange exchange) throws IOException {
        String jsonResponse = gson.toJson(manager.getTasks());
        sendText(exchange, jsonResponse);
    }
}
