import java.nio.file.Files;
import java.util.*;
import java.io.*;
import java.nio.*;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(toString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                writer.write(toString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);

        try {
            String content = Files.readString(file.toPath());
            String[] lines = content.split("\n");

            for (int i = 1; i < lines.length; i++) {
                if (lines[i].isBlank()) continue;
                Task task = fromString(lines[i]);
                switch (task.getType()) {
                    case TASK -> manager.createTask(task);
                    case EPIC -> manager.createEpic((Epic) task);
                    case SUBTASK -> manager.createSubTask((SubTask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке файла", e);
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] fields = value.split(",");
        int id = Integer.parseInt(fields[0]);
        TaskType type = TaskType.valueOf(fields[1]);
        String title = fields[2];
        Status status = Status.valueOf(fields[3]);
        String description = fields[4];

        switch (type) {
            case TASK -> {
                Task task = new Task(title, description);
                task.setId(id);
                task.setStatus(status);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic();
                epic.setId(id);
                epic.setTitle(title);
                epic.setDescription(description);
                epic.setStatus(status);
                return epic;
            }
            case SUBTASK -> {
                SubTask subTask = new SubTask();
                subTask.setId(id);
                subTask.setTitle(title);
                subTask.setDescription(description);
                subTask.setStatus(status);
                int epicId = Integer.parseInt(fields[5]);
                subTask.setEpicId(epicId);
                return subTask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }

    private static String toString(Task task) {
        String base =  task.getId() + ", " + task.getType() + ", " +
                task.getTitle() + ", " + task.getStatus() + ", " +
                task.getDescription();
        if (task instanceof SubTask) {
            return base + "," + ((SubTask) task).getEpicId();
        }
        if (task instanceof Epic) {
            return base + ",";
        }
        return base;
    }
}
