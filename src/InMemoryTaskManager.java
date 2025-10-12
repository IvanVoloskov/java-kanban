import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private int newId = 1;

    private final HashMap<Integer,Task> tasks = new HashMap<>();
    private final HashMap<Integer,Epic> epics = new HashMap<>();
    private final HashMap<Integer,SubTask> subTasks = new HashMap<>();
    private final HistoryManager historyManager;

    private final Comparator<Task> comparator = new Comparator<>() {
        @Override
        public int compare(Task t1, Task t2) {
            return t1.getStartTime().compareTo(t2.getStartTime());
        }
    };

    private final Set<Task> prioritizedTasks = new TreeSet<>(comparator);


    public InMemoryTaskManager(HistoryManager historyManager) {
        this.historyManager = historyManager;
    }

    @Override
    public Task createTask(Task task) {
        if (task.getStartTime() != null && checkIntersections(task)) {
            throw new IllegalArgumentException("Невозможно добавить задачу, " +
                    "так как задача пересекается с другой по времени выполнения");
        }
        task.setId(newId);
        newId++;
        tasks.put(task.getId(), task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        epic.setId(newId);
        newId++;
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (subTask.getStartTime() != null && checkIntersections(subTask)) {
            throw new IllegalArgumentException("Невозможно добавить подзадачу, " +
                    "так как подзадача пересекается с другой по времени выполнения");
        }
        subTask.setId(newId);
        newId++;
        subTasks.put(subTask.getId(), subTask);
        Epic epic = epics.get(subTask.getEpicId());
        if (epic != null) {
            epic.addSubTaskId(subTask.getId());
            updateEpicStatus(epic.getId());
            updateEpicTime(epic);
        }
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
        return subTask;
    }

    @Override
    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<SubTask> getAllSubTasks() {
        return new ArrayList<>(subTasks.values());
    }

    @Override
    public void removeAllTasks() {
        tasks.keySet().forEach(historyManager::remove);
        tasks.clear();
    }

    @Override
    public void removeAllEpics() {
        epics.keySet().forEach(historyManager::remove);
        subTasks.keySet().forEach(historyManager::remove);
        epics.clear();
        subTasks.clear();

    }

    @Override
    public void removeAllSubTasks() {
        subTasks.keySet().forEach(historyManager::remove);
        subTasks.clear();
        epics.values().forEach(epic -> {
            epic.getSubTaskId().clear();
            epic.setStatus(Status.NEW);
        });

    }

    @Override
    public Task getTaskForId(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicForId(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public SubTask getSubTaskForId(int id) {
        SubTask subTask = subTasks.get(id);
        if (subTask != null) {
            historyManager.add(subTask);
        }
        return subTask;
    }

    @Override
    public void updateTask(Task task) {
        prioritizedTasks.remove(task);
        if (task.getStartTime() != null && checkIntersections(task)) {
            throw new IllegalArgumentException("Обновить задачу не получиться," +
                    " так как задача пересекается с другой задачей по времени");
        }
        int id = task.getId();
        if (!tasks.containsKey(id)) {
            System.out.println("Задачи с таким id нет");
            return;
        }
        tasks.put(id, task);
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        int id = epic.getId();
        if (!epics.containsKey(id)) {
            System.out.println("Эпика с таким id нет");
            return;
        }
        Epic epicNew = epics.get(id);
        epicNew.setTitle(epic.getTitle());
        epicNew.setDescription(epic.getDescription());
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        prioritizedTasks.remove(subTask);
        if (subTask.getStartTime() != null && checkIntersections(subTask)) {
            throw new IllegalArgumentException("Невозможно обновить подзадачу," +
                    " так как подзадача пересекается с другой подзадачей по времени");
        }
        int id = subTask.getId();
        if (!subTasks.containsKey(id)) {
            System.out.println("Подзадачи с таким id нет");
            return;
        }
        subTasks.put(id, subTask);
        updateEpicStatus(subTask.getEpicId());
        if (subTask.getStartTime() != null) {
            prioritizedTasks.add(subTask);
        }
    }

    @Override
    public void removeTaskById(int id) {
        if (tasks.containsKey(id)) {
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
        } else {
            System.out.println("Задачи с таким id нет");
        }
    }

    @Override
    public void removeEpicById(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskId()) {
                subTasks.remove(subtaskId);
                historyManager.remove(subtaskId);
            }
            epic.getSubTaskId().clear();
            historyManager.remove(id);
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        SubTask subTask = subTasks.remove(id);
        if (subTask != null) {
            prioritizedTasks.remove(subTask);
            Epic epic = epics.get(subTask.getEpicId());
            if (epic != null) {
                epic.removeSubTaskId(id);
                updateEpicStatus(epic.getId());
                updateEpicTime(epic);
            }
            historyManager.remove(id);
        } else {
            System.out.println("Подзадачи с таким id нет");
        }
    }

    @Override
    public List<SubTask> getSubTasksByEpicId(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return Collections.emptyList();
        return epic.getSubTaskId().stream()
                .map(subTasks::get)
                .filter(Objects::nonNull)
                .toList();
    }

    private void updateEpicStatus(int epicId) {
        Epic epic = epics.get(epicId);
        if (epic == null) return;

        List<Integer> subId = epic.getSubTaskId();
        if (subId.isEmpty()) {
            epic.setStatus(Status.NEW);
            return;
        }

        boolean allNew = true;
        boolean allDone = true;

        for (Integer id : subId) {
            SubTask subTask = subTasks.get(id);
            if (subTask == null) continue;

            if (subTask.getStatus() != Status.NEW) {
                allNew = false;
            }
            if (subTask.getStatus() != Status.DONE) {
                allDone = false;
            }
        }

        if (allNew) {
            epic.setStatus(Status.NEW);
        } else if (allDone) {
            epic.setStatus(Status.DONE);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    public void updateEpicTime(Epic epic) {
        List<Integer> subTaskIds = epic.getSubTaskId();
        if (subTaskIds.isEmpty()) {
            epic.setDuration(Duration.ZERO);
            epic.setStartTime(null);
            return;
        }

        LocalDateTime start = null;
        LocalDateTime end = null;
        long totalDuration = 0;

        for (Integer id : subTaskIds) {
            SubTask sub = subTasks.get(id);
            if (sub == null || sub.getDuration() == null || sub.getStartTime() == null) {
                continue;
            }
            LocalDateTime subStart = sub.getStartTime();
            LocalDateTime subEnd = sub.getEndTime();
            totalDuration += sub.getDuration().toMinutes();

            if (start == null || subStart.isBefore(start)) {
                start = subStart;
            }

            if (end == null || subEnd.isAfter(end)) {
                end = subEnd;
            }
        }

        epic.setDuration(Duration.ofMinutes(totalDuration));
        epic.setStartTime(start);
        epic.setEndTime(end);
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    public boolean isIntersecting (Task t1, Task t2) {
        if (t1.getStartTime() == null || t1.getEndTime() == null) return false;
        if (t2.getStartTime() == null || t2.getEndTime() == null) return false;
        return t1.getEndTime().isAfter(t2.getStartTime()) && t1.getStartTime().isBefore(t2.getEndTime());
    }

    public boolean checkIntersections (Task newTask) {
        return prioritizedTasks.stream()
                .anyMatch(task -> isIntersecting(newTask, task));
    }
}
