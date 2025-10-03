public class SubTask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        if (epicId == this.getId()) {
            System.out.println("Подзадача не может быть своим же эпиком");
            return;
        }
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{id=" + getId() + ", title='" + getTitle() + "', status=" + getStatus() +
                ", epicId=" + epicId + "}";
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

}