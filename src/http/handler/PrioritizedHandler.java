package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.GsonFactory;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.util.List;


public class PrioritizedHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = GsonFactory.createGson();

    public PrioritizedHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            if (method.equals("GET") || path.equals("/prioritized")) {
                List<Task> prioritizedTasks = manager.getPrioritizedTasks();
                sendText(exchange, gson.toJson(prioritizedTasks));
            } else {
                sendNotFound(exchange, "Данный метод не поддерживается");
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (Exception e) {
            internalServerError(exchange, e.getMessage());
        }
    }
}
