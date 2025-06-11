import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager manager;

    @BeforeEach
    void init() {
        HistoryManager history = Managers.getDefaultHistory();
        manager = new InMemoryTaskManager(history);
    }

    @Test
    void canAddAndRetrieveTasks() {
        Task task = new Task("Сходить в парк", "Покормить голубей");
        manager.createTask(task);
        Task id = manager.getTaskForId(task.getId());
        assertEquals(id, task);
    }

    @Test
    void doesNotConflictIds() {
        Task task1 = new Task("Сходить в парк", "Покормить голубей");
        task1.setId(10);
        manager.createTask(task1);
        Task task2 = new Task("Сходить в магазин", "Купить колбасу");
        manager.createTask(task2);
        assertNotEquals(task1.getId(), task2.getId());
    }

    @Test
    void addingTaskDoesNotChangeIt() {
        Task task1 = new Task("Сходить в парк", "Покормить голубей");
        task1.setId(1);
        manager.createTask(task1);
        Task sameTask = manager.getTaskForId(task1.getId());
        assertEquals("Сходить в парк", sameTask.getTitle());
        assertEquals("Покормить голубей", sameTask.getDescription());
        assertEquals(Status.NEW, sameTask.getStatus());
    }
}