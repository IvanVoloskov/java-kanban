package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.GsonFactory;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TaskHandler extends BaseHttpHandler implements HttpHandler {

    private final TaskManager manager;
    private final Gson gson = GsonFactory.createGson();

    public TaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET" -> {
                    if (path.matches("/tasks/\\d+")) { // /tasks/{id}
                        int id = Integer.parseInt(path.split("/")[2]);
                        Task task = manager.getTaskForId(id);
                        if (task == null) {
                            sendNotFound(exchange, "Задача с id " + id + " не найдена");
                            return;
                        }
                        sendText(exchange, gson.toJson(task));
                    } else if (path.equals("/tasks")) {
                        sendText(exchange, gson.toJson(manager.getAllTasks()));
                    }
                }
                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getId() == 0) {
                        manager.createTask(task);
                        sendText(exchange, "Задача создана");
                    } else {
                        manager.updateTask(task);
                        sendText(exchange, "Задача обновлена");
                    }
                }
                case "DELETE" -> {
                    if (path.matches("/tasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        manager.removeTaskById(id);
                        sendText(exchange, "Задача удалена");
                    } else {
                        sendText(exchange, "Метод DELETE поддерживается только с указанием ID");
                    }
                }
                default -> sendNotFound(exchange, "Метод не поддерживается");
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (Exception e) {
            internalServerError(exchange, e.getMessage());
        }
    }
}
