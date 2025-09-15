import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

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

    @Test
    void historyPreservesOrder() {
        Task t1 = new Task("Сходить в парк", "Покормить голубей"); manager.createTask(t1);
        Task t2 = new Task("Сходить в магазин", "Купить колбасу"); manager.createTask(t2);

        manager.getTaskForId(t1.getId());
        manager.getTaskForId(t2.getId());

        List<Task> history = manager.getHistory();
        assertEquals(List.of(t1, t2), history, "История должна сохранять порядок просмотров");
    }

    @Test
    void epicDoesNotContainDeletedSubTaskIds() {
        Epic epic = new Epic();
        epic.setTitle("Организация праздника");
        manager.createEpic(epic);

        SubTask sub1 = new SubTask();
        sub1.setTitle("Пригласить гостей");
        sub1.setEpicId(epic.getId());
        manager.createSubTask(sub1);

        SubTask sub2 = new SubTask();
        sub2.setTitle("Заказать торт");
        sub2.setEpicId(epic.getId());
        manager.createSubTask(sub2);

        // Удаляем подзадачу
        manager.removeSubTaskById(sub1.getId());

        Epic updatedEpic = manager.getEpicForId(epic.getId());
        assertFalse(updatedEpic.getSubTaskId().contains(sub1.getId()));
        assertTrue(updatedEpic.getSubTaskId().contains(sub2.getId()));
    }

    @Test
    void epicSubTasksOrderMaintained() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub1 = new SubTask(); sub1.setEpicId(epic.getId()); manager.createSubTask(sub1);
        SubTask sub2 = new SubTask(); sub2.setEpicId(epic.getId()); manager.createSubTask(sub2);
        SubTask sub3 = new SubTask(); sub3.setEpicId(epic.getId()); manager.createSubTask(sub3);

        manager.removeSubTaskById(sub2.getId());

        List<Integer> subTaskIds = manager.getEpicForId(epic.getId()).getSubTaskId();
        assertEquals(List.of(sub1.getId(), sub3.getId()), subTaskIds);
    }
}