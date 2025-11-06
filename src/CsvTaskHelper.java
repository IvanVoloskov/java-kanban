import model.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CsvTaskHelper {

    public static String parseToString(Task task) {
        String duration = (task.getDuration() != null) ? String.valueOf(task.getDuration().toMinutes()) : "";
        String timeStart = (task.getStartTime() != null) ? (task.getStartTime().toString()) : "";

        String base = task.getId() + "," + task.getType() + "," +
                task.getTitle() + "," + task.getStatus() + "," +
                task.getDescription() + "," + duration + "," + timeStart;

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
        String start = (fields.length > 6) ? fields[6] : "";
        long durationMinutes = (fields.length > 5 && fields[5] != null && !fields[5].isEmpty())
                ? Long.parseLong(fields[5]) : 0;
        LocalDateTime startTime = start.isEmpty() ? null : LocalDateTime.parse(start);


        switch (type) {
            case TASK -> {
                Task task = new Task(title, description);
                task.setId(id);
                task.setStatus(status);
                task.setDuration(Duration.ofMinutes(durationMinutes));
                task.setStartTime(startTime);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic();
                epic.setId(id);
                epic.setTitle(title);
                epic.setDescription(description);
                epic.setStatus(status);
                epic.setDuration(Duration.ofMinutes(durationMinutes));
                epic.setStartTime(startTime);
                return epic;
            }
            case SUBTASK -> {
                SubTask subTask = new SubTask();
                subTask.setId(id);
                subTask.setTitle(title);
                subTask.setDescription(description);
                subTask.setStatus(status);
                subTask.setDuration(Duration.ofMinutes(durationMinutes));
                subTask.setStartTime(startTime);
                int epicId = (fields.length > 7) ? Integer.parseInt(fields[7]) : 0;
                subTask.setEpicId(epicId);
                return subTask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
