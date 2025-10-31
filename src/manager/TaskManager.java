package manager;

import model.Epic;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.List;

public interface TaskManager {
    Task createTask(Task task);

    Epic createEpic(Epic epic);

    SubTask createSubTask(SubTask subTask);

    ArrayList<Task> getAllTasks();

    ArrayList<Epic> getAllEpics();

    ArrayList<SubTask> getAllSubTasks();

    void removeAllTasks();

    void removeAllEpics();

    void removeAllSubTasks();

    Task getTaskForId(int id);

    Epic getEpicForId(int id);

    SubTask getSubTaskForId(int id);

    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubTask(SubTask subTask);

    void removeTaskById(int id);

    void removeEpicById(int id);

    void removeSubTaskById(int id);

    List<SubTask> getSubTasksByEpicId(int epicId);

    List<Task> getHistory();

    List<Task> getPrioritizedTasks();
}
