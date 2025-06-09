import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TaskTest {
    @Test
    void tasksAreEqualIfIdIsEqual() {
        Task task1 = new Task("Сходить в магазин", "Купить хлеб");
        Task task2 = new Task("Купить билет в кино" ,"Сесть на 3 ряду");
        task1.setId(1);
        task2.setId(1);
        assertEquals(task1, task2);
    }
}