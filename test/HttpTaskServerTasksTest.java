package http;

import com.google.gson.Gson;
import manager.Managers;
import manager.TaskManager;
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

public class HttpTaskServerTasksTest {

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
    public void testAddTaskAndGetAllAndGetById() throws IOException, InterruptedException {
        Task task = new Task("Test Task 1", "Описание тестовой задачи");
        String json = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        URI url = URI.create("http://localhost:8080/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(url)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "Ожидали 200 при создании задачи");

        List<Task> tasks = manager.getAllTasks();
        assertNotNull(tasks, "Список задач не должен быть null");
        assertEquals(1, tasks.size(), "Ожидали одну задачу в менеджере");
        assertEquals("Test Task 1", tasks.get(0).getTitle());

        int id = tasks.get(0).getId();
        HttpRequest getById = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .GET()
                .build();
        HttpResponse<String> respGet = client.send(getById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respGet.statusCode(), "Ожидали 200 при получении задачи по id");
        // тело ответа должно содержать title
        assertTrue(respGet.body().contains("Test Task 1"));
    }

    @Test
    public void testGetNonExistingTaskReturns404() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/99999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode(), "Ожидали 404 для несуществующего id");
    }
}
