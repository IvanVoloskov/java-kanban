import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {
    @Test
    void subTasksAreEqualIfIdIsEqual() {
        SubTask subTask1 = new SubTask();
        SubTask subTask2 = new SubTask();
        subTask1.setId(3);
        subTask2.setId(3);
        assertEquals(subTask1, subTask2);
    }

    @Test
    void subTaskCannotBeItsOwnEpic() {
        SubTask subTask1 = new SubTask();
        subTask1.setId(2);
        subTask1.setEpicId(2);
        assertNotEquals(subTask1.getId(), subTask1.getEpicId(), "Subtask should not be its own epic");
    }
}