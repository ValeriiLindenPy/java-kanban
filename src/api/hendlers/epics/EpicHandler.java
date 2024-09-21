package api.hendlers.epics;


import api.hendlers.BaseHttpHandler;
import com.sun.net.httpserver.HttpExchange;
import model.Epic;

import java.io.IOException;
import java.util.HashSet;

public class EpicHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetAllEpics(exchange);
            case "POST" -> handleCreateEpic(exchange);
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
            exchange.close();
        } else {
            manager.updateEpicTask(epic);
            sendCode(exchange, 201);
        }
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
