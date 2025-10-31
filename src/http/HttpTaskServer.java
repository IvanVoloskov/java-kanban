package http;

import com.sun.net.httpserver.HttpServer;
import http.handler.*;
import manager.TaskManager;
import manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080; // фиксированный порт
    private final TaskManager manager;
    private HttpServer server;

    public HttpTaskServer(TaskManager manager) {
        this.manager = manager;
    }

    public void start() {
        try {
            server = HttpServer.create(new InetSocketAddress(PORT), 0);

            // Привязываем обработчики к путям
            server.createContext("/tasks", new TaskHandler(manager));
            server.createContext("/subtasks", new SubTaskHandler(manager));
            server.createContext("/epics", new EpicHandler(manager));
            server.createContext("/history", new HistoryHandler(manager));
            server.createContext("/prioritized", new PrioritizedHandler(manager));

            server.start();
            System.out.println("Сервер успешно запущен на порту " + PORT + "!");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            System.out.println("Сервер остановлен");
        }
    }

    public static void main(String[] args) {
        TaskManager manager = Managers.getDefault();
        HttpTaskServer server = new HttpTaskServer(manager);
        server.start();
    }
}
