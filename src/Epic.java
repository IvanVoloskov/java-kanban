import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskId = new ArrayList<>();

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void addSubTaskId(int id) {
        subTaskId.add(id);
    }

    public void removeSubTaskId(Integer id) {
        subTaskId.remove(id);
    }

    @Override
    public String toString() {
        return "Epic{id=" + getId() + ", title='" + getTitle() + "', status=" + getStatus() +
                ", subTasks=" + subTaskId + "}";
    }
}