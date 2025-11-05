package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.GsonFactory;
import manager.TaskManager;
import model.SubTask;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = GsonFactory.createGson();

    public SubTaskHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        try {
            switch (method) {
                case "GET" -> {
                    if (path.matches("/subtasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        SubTask subTask = manager.getSubTaskForId(id);
                        if (subTask == null) {
                            sendNotFound(exchange, "Подзадачи с id " + id + " не найдено");
                            return;
                        }
                        sendText(exchange, gson.toJson(subTask));
                    } else if (path.equals("/subtasks")) {
                        sendText(exchange, gson.toJson(manager.getAllSubTasks()));
                    }
                }
                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    SubTask subTask = gson.fromJson(body, SubTask.class);
                    if (subTask.getId() == 0) {
                        manager.createSubTask(subTask);
                        sendText(exchange, "Подзадача создана");
                    } else {
                        manager.updateSubTask(subTask);
                        sendText(exchange, "Подзадача обновлена");
                    }
                }
                case "DELETE" -> {
                    if (path.equals("/subtasks/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        manager.removeSubTaskById(id);
                        sendText(exchange, "Подзадача удалена");
                    } else {
                        sendText(exchange, "Удаление доступно только с указанием ID подзадачи");
                    }
                }
                default -> {
                    sendNotFound(exchange, "Данный метод не поддерживается");
                    return;
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (Exception e) {
            internalServerError(exchange, e.getMessage());
        }
    }
}
