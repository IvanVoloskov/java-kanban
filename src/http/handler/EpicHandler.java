package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import http.BaseHttpHandler;
import http.GsonFactory;
import manager.TaskManager;
import model.Epic;
import model.SubTask;

import java.io.IOException;
import java.util.List;

public class EpicHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson = GsonFactory.createGson();

    public EpicHandler(TaskManager manager) {
        this.manager = manager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();

        try {
            switch (method) {
                case "GET" -> {
                    if (path.matches("/epics/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = manager.getEpicForId(id);
                        if (epic == null) {
                            sendNotFound(exchange, "Эпика с id " + id + " не найдено");
                            return;
                        } else {
                            sendText(exchange, gson.toJson(epic));
                        }
                    } else if (path.equals("/epics")) {
                        sendText(exchange, gson.toJson(manager.getAllEpics()));
                    } else if (path.matches("/epics/\\d+/subtasks")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = manager.getEpicForId(id);
                        if (epic == null) {
                            sendNotFound(exchange, "Эпик с id " + id + " не найден");
                            return;
                        } else {
                            List<SubTask> subTasks = manager.getSubTasksByEpicId(id);
                            sendText(exchange, gson.toJson(subTasks));
                        }
                    }
                }
                case "POST" -> {
                    String body = new String(exchange.getRequestBody().readAllBytes());
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (path.equals("/epics") || epic != null) {
                        manager.createEpic(epic);
                        sendText(exchange, "Эпик добавлен");
                    } else {
                        sendText(exchange, "Можно добавить только новый эпик");
                    }
                }
                case "DELETE" -> {
                    if (path.matches("/epics/\\d+")) {
                        int id = Integer.parseInt(path.split("/")[2]);
                        Epic epic = manager.getEpicForId(id);
                        if (epic == null) {
                            sendText(exchange, "Эпика с id " + id + " не найдено");
                        } else {
                            manager.removeEpicById(id);
                            sendText(exchange, "Эпик удалён");
                        }
                    }
                }
                default -> {
                    sendNotFound(exchange, "Данный метод не поддерживается");
                }
            }
        } catch (IllegalArgumentException e) {
            sendHasOverlaps(exchange, e.getMessage());
        } catch (Exception e) {
            internalServerError(exchange, e.getMessage());
        }
    }
}
