package http;

import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.SubTask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerEpicsAndSubtasksTest {
    private TaskManager manager;
    private HttpTaskServer server;
    private final Gson gson = GsonFactory.createGson();

    @BeforeEach
    public void setUp() {
        manager = Managers.getDefault();
        manager.removeAllTasks();
        manager.removeAllEpics();
        manager.removeAllSubTasks();

        server = new HttpTaskServer(manager);
        server.start();
    }

    @AfterEach
    public void tearDown() {
        server.stop();
    }

    @Test
    public void testAddEpicAndSubtaskAndRelation() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        // 1) Добавляем эпик
        Epic epic = new Epic();
        epic.setTitle("Epic 1");
        epic.setDescription("Описание эпика 1");

        String epicJson = gson.toJson(epic);
        HttpRequest postEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> respEpic = client.send(postEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respEpic.statusCode(), "Ожидали 200 при создании эпика");

        List<Epic> epics = manager.getAllEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");
        int epicId = epics.get(0).getId();

        SubTask subTask = new SubTask();
        subTask.setTitle("Subtask 1");
        subTask.setDescription("Описание подзадачи");
        subTask.setEpicId(epicId); // связываем

        String subJson = gson.toJson(subTask);
        HttpRequest postSub = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> respSub = client.send(postSub, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respSub.statusCode(), "Ожидали 200 при создании подзадачи");

        List<SubTask> subs = manager.getAllSubTasks();
        assertEquals(1, subs.size(), "Ожидали одну подзадачу");
        int subId = subs.get(0).getId();

        Epic storedEpic = manager.getEpicForId(epicId);
        assertNotNull(storedEpic, "Эпик должен существовать");
        assertTrue(storedEpic.getSubTaskId().contains(subId), "Эпик должен содержать id подзадачи");

        HttpRequest getSubtasksForEpic = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId + "/subtasks"))
                .GET().build();
        HttpResponse<String> respList = client.send(getSubtasksForEpic, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respList.statusCode(), "Ожидали 200 при получении подзадач эпика");
        assertTrue(respList.body().contains("Subtask 1"));
    }

    @Test
    public void testGetNonExistingEpicReturns404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/99999"))
                .GET().build();
        HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
    }
}
