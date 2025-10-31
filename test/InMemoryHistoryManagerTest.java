import java.util.List;

import manager.InMemoryHistoryManager;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private InMemoryHistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void historyRemovesOldNodeWhenAddedAgain() {
        Task task = new Task("Купить машину", "Заправить машину");
        task.setId(1);

        historyManager.add(task);
        historyManager.add(task);

        List<Task> tasks = historyManager.getHistory();
        assertEquals(1, tasks.size(), "В истории должна остаться только одна копия задачи");
    }

    @Test
    void removeTaskByIdRemovesFromHistory() {
        Task task1 = new Task("Купить машину", "Заправить машину");
        task1.setId(1);
        Task task2 = new Task("Устроиться на работу", "Получить зарплату");
        task2.setId(2);
        Task task3 = new Task("Купить PS5", "Скачать игру");
        task3.setId(3);

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);

        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size(), "Должны остаться 2 задачи");
        assertFalse(history.contains(task2), "Удалённая задача не должна присутствовать в истории");
        assertEquals(List.of(task1, task3), history, "Остальные задачи должны сохранить порядок");
    }

}
