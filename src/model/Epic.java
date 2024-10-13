package model;

import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(int i, String name, String description, Status aNew) {
        super(name, description);
    }

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.setStatus(Status.NEW);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
    }

    public void clearSubtasks() {
        subtaskList.clear();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
    }

    @Override
    public String toString() {
        return "model.Epic{" +
                "name= " + getName() + '\'' +
                ", description = " + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size = " + subtaskList.size() +
                ", status = " + getStatus() +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Epic other = (Epic) obj;
        return getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }
    }
