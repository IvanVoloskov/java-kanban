package model;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subTaskId = new ArrayList<>();
    private LocalDateTime endTime;

    public ArrayList<Integer> getSubTaskId() {
        return subTaskId;
    }

    public void addSubTaskId(int id) {
        if (id == this.getId()) {
            System.out.println("Эпик не может содержать самого себя, как подзадачу");
        } else {
            subTaskId.add(id);
        }
    }

    public void removeSubTaskId(Integer id) {
        subTaskId.remove(id);
    }

    @Override
    public String toString() {
        return "model.Epic{id=" + getId() + ", title='" + getTitle() + "', status=" + getStatus() +
                ", subTasks=" + subTaskId + "}";
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}