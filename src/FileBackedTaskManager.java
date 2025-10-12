import java.io.*;
import java.nio.file.Files;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File file;

    public FileBackedTaskManager(HistoryManager historyManager, File file) {
        super(historyManager);
        this.file = file;
    }


    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic\n");
            for (Task task : getAllTasks()) {
                writer.write(CsvTaskHelper.parseToString(task) + "\n");
            }
            for (Epic epic : getAllEpics()) {
                writer.write(CsvTaskHelper.parseToString(epic) + "\n");
            }
            for (SubTask subTask : getAllSubTasks()) {
                writer.write(CsvTaskHelper.parseToString(subTask) + "\n");
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }


    public static FileBackedTaskManager loadFromFile(File file, HistoryManager historyManager) {
        FileBackedTaskManager manager = new FileBackedTaskManager(historyManager, file);
        if (!file.exists()) {
            return manager; // просто возвращаем пустой менеджер
        }
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.isBlank()) continue;
                Task task = CsvTaskHelper.parseFromString(line);
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



    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        SubTask created = super.createSubTask(subTask);
        save();
        return created;
    }

    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    @Override
    public Task getTaskForId(int id) {
        Task getTask = super.getTaskForId(id);
        save();
        return getTask;
    }

    @Override
    public Epic getEpicForId(int id) {
        Epic getEpic = super.getEpicForId(id);
        save();
        return getEpic;
    }

    @Override
    public SubTask getSubTaskForId(int id) {
        SubTask getSubTask = super.getSubTaskForId(id);
        save();
        return getSubTask;
    }
}
