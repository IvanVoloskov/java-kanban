import java.util.Objects;

public class Task {
    private String title;
    private String description;
    private int id;
    private Status status;

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Task() {
        this.status = Status.NEW;
    }

    public Task(String title, String description) {
        this.title = title;
        this.description = description;
        this.status = Status.NEW;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task task = (Task) obj;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title='" + title + "', status=" + status + "}";
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TaskType getType() {
        return TaskType.TASK;
    }
}