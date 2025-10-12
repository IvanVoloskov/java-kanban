import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {
    protected T manager;

    // Метод, который будет реализован в конкретных классах
    protected abstract T createManager();

    @BeforeEach
    void setup() {
        manager = createManager();
    }

    // ===== Тесты для Epic =====
    @Test
    void epicStatus_allNew() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub1 = new SubTask();
        sub1.setEpicId(epic.getId());
        manager.createSubTask(sub1);

        SubTask sub2 = new SubTask();
        sub2.setEpicId(epic.getId());
        manager.createSubTask(sub2);

        assertEquals(Status.NEW, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_allDone() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub1 = new SubTask();
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.DONE);
        manager.createSubTask(sub1);

        SubTask sub2 = new SubTask();
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.DONE);
        manager.createSubTask(sub2);

        assertEquals(Status.DONE, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_mixedStatus() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub1 = new SubTask();
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.NEW);
        manager.createSubTask(sub1);

        SubTask sub2 = new SubTask();
        sub2.setEpicId(epic.getId());
        sub2.setStatus(Status.DONE);
        manager.createSubTask(sub2);

        assertEquals(Status.IN_PROGRESS, manager.getEpicForId(epic.getId()).getStatus());
    }

    @Test
    void epicStatus_inProgressOnly() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub1 = new SubTask();
        sub1.setEpicId(epic.getId());
        sub1.setStatus(Status.IN_PROGRESS);
        manager.createSubTask(sub1);

        assertEquals(Status.IN_PROGRESS, manager.getEpicForId(epic.getId()).getStatus());
    }

    // ===== Тесты пересечения времени =====
    @Test
    void checkTaskTimeIntersections() {
        Task t1 = new Task("Task1", "desc");
        t1.setStartTime(LocalDateTime.of(2025, 10, 12, 10, 0));
        t1.setDuration(Duration.ofHours(2));
        manager.createTask(t1);

        Task t2 = new Task("Task2", "desc");
        t2.setStartTime(LocalDateTime.of(2025, 10, 12, 11, 0));
        t2.setDuration(Duration.ofHours(1));

        assertThrows(IllegalArgumentException.class, () -> manager.createTask(t2),
                "Должно выбрасывать исключение при пересечении задач");
    }

    // ===== Тесты истории =====
    @Test
    void getHistory_empty() {
        List<Task> history = manager.getHistory();
        assertTrue(history.isEmpty(), "История пустая для нового менеджера");
    }

    // ===== Тесты удаления =====
    @Test
    void removeTaskById_removesFromManagerAndHistory() {
        Task task = new Task("Title", "Desc");
        manager.createTask(task);
        manager.getTaskForId(task.getId());
        manager.removeTaskById(task.getId());

        assertNull(manager.getTaskForId(task.getId()));
        assertTrue(manager.getHistory().isEmpty());
    }

    @Test
    void removeEpic_removesSubTasks() {
        Epic epic = new Epic();
        manager.createEpic(epic);

        SubTask sub = new SubTask();
        sub.setEpicId(epic.getId());
        manager.createSubTask(sub);

        manager.removeEpicById(epic.getId());

        assertNull(manager.getEpicForId(epic.getId()));
        assertTrue(manager.getAllSubTasks().isEmpty());
    }
}
