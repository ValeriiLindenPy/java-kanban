package api;

import api.urls.Routs;
import com.sun.net.httpserver.HttpServer;
import service.utils.customExceptions.ServerRunException;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    public final HttpServer httpServer;

    public HttpTaskServer() throws IOException {
        this.httpServer = HttpServer.create(new InetSocketAddress(ServerSettings.PORT), 0);
        Routs.getRouts().forEach((rout, handler) -> this.httpServer.createContext(rout,handler));
    }

    public void start() {
        this.httpServer.start();
        System.out.println("Server started on port " + ServerSettings.PORT);
    }

    public void stop() {
        this.httpServer.stop(1);
        System.out.println("Server stopped");
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();

        } catch (IOException ioe) {
            throw new ServerRunException("Oops! Server problem!", ioe);
        }
    }
}
