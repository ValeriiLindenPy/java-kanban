package api.hendlers;

import api.ServerSettings;
import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import service.interfaces.TaskManager;

import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseHttpHandler implements HttpHandler {
    protected Gson gson = ServerSettings.gson;
    protected TaskManager manager = ServerSettings.manager;

    protected void sendText(HttpExchange he, String text) throws IOException {
        byte[] resp = text.getBytes(ServerSettings.charset);
        he.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        he.sendResponseHeaders(200, resp.length);

        try (OutputStream os = he.getResponseBody()) {
            os.write(resp);
        }
    }

    protected void sendCode(HttpExchange he, int code) throws IOException {
        he.sendResponseHeaders(code, -1);
        he.close();
    }

    public abstract void handle(HttpExchange exchange) throws IOException;
}
