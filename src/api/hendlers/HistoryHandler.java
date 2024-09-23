package api.hendlers;


import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        switch (method) {
            case "GET" -> handleGetHistory(exchange);
        }
    }

    private void handleGetHistory(HttpExchange exchange) throws IOException {
        try {
            String jsonResponse = gson.toJson(manager.getHistory());
            sendText(exchange, jsonResponse);
        } catch (Exception e) {
            sendText(exchange, e.getMessage());
        }
    }

}
