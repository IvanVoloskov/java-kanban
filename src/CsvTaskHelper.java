public class CsvTaskHelper {

    public static String parseToString(Task task) {
        String base = task.getId() + "," + task.getType() + "," +
                task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription();
        if (task instanceof SubTask) {
            return base + "," + ((SubTask) task).getEpicId();
        }
        if (task instanceof Epic) {
            return base + ",";
        }
        return base;
    }

    public static Task parseFromString(String value) {
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
}
