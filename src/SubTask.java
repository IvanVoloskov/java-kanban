public class SubTask extends Task {
    private int epicId;

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return "SubTask{id=" + getId() + ", title='" + getTitle() + "', status=" + getStatus() +
                ", epicId=" + epicId + "}";
    }

}