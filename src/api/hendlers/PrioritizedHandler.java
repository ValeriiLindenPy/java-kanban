package api.hendlers;


import com.sun.net.httpserver.HttpExchange;
import model.Task;
import service.utils.customExceptions.IntersectionTaskException;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {


    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetPrioritized(exchange);
        }

    }


    private void handleGetPrioritized(HttpExchange exchange) throws IOException {

        try {
            String jsonResponse = gson.toJson(manager.getPrioritizedTasks());
            sendText(exchange, jsonResponse);
        } catch (Exception e) {
            sendText(exchange, e.getMessage());
        }
    }

}
