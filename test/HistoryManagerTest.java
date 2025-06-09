import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class HistoryManagerTest {
    private HistoryManager historyManager;
    private Task task;

    @BeforeEach
    void setUp() {
        historyManager = Managers.getDefaultHistory();
        task = new Task("Проснуться", "Почистить зубы");
        task.setId(100);
    }

    @Test
    void taskAddedToHistory() {
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    void historyPreservesTaskData() {
        task.setTitle("Начальное");
        historyManager.add(task);
        task.setTitle("Измененное");
        List<Task> history = historyManager.getHistory();
        assertEquals("Измененное", history.get(0).getTitle(), "Название должно отражать последнее состояние объекта.");
    }
}