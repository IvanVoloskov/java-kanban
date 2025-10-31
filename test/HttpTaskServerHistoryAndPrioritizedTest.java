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

public class HttpTaskServerHistoryAndPrioritizedTest {
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
    public void testHistoryContainsAccessedTasks() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        Task task = new Task("HistoryTask", "Для истории");
        String json = gson.toJson(task);

        HttpRequest post = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> respPost = client.send(post, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respPost.statusCode());

        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        int id = tasks.get(0).getId();

        HttpRequest getById = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + id))
                .GET().build();
        HttpResponse<String> respGet = client.send(getById, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respGet.statusCode());

        HttpRequest getHistory = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET().build();
        HttpResponse<String> respHistory = client.send(getHistory, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, respHistory.statusCode());
        assertTrue(respHistory.body().contains("HistoryTask"));
    }

    @Test
    public void testPrioritizedReturns200AndArray() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest get = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET().build();
        HttpResponse<String> response = client.send(get, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());
        assertTrue(response.body().startsWith("["));
    }
}
