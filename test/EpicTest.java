import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicsAreEqualIfIdIsEqual() {
        Epic epic1 = new Epic();
        Epic epic2 = new Epic();
        epic1.setId(1);
        epic2.setId(1);
        assertEquals(epic1, epic2);
    }

    @Test
    void epicCannotContainItself() {
        Epic epic = new Epic();
        epic.setId(1);
        epic.addSubTaskId(1);
        assertFalse(epic.getSubTaskId().contains(epic.getId()));
    }
}