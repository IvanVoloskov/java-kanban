import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void defaultManagersNotNull() {
        assertNotNull(Managers.getDefault(), "TaskManager не может быть пустым");
        assertNotNull(Managers.getDefaultHistory(), "HistoryManager не может быть пустым");
    }
}