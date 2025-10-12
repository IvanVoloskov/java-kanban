import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @Override
    protected FileBackedTaskManager createManager() {
        file = new File("tasks_test.csv");
        return new FileBackedTaskManager(Managers.getDefaultHistory(), file);
    }

    @Test
    void saveAndLoadFromFile() {
        Task task = new Task("Task", "Desc");
        manager.createTask(task);

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file, Managers.getDefaultHistory());
        Task loadedTask = loadedManager.getTaskForId(task.getId());

        assertEquals(task.getTitle(), loadedTask.getTitle());
        assertEquals(task.getDescription(), loadedTask.getDescription());
    }

    @Test
    void loadFromNonexistentFileDoesNotThrow() {
        File nonexistent = new File("nonexistent.csv");
        assertDoesNotThrow(() -> FileBackedTaskManager.loadFromFile(nonexistent, Managers.getDefaultHistory()));
    }
}
