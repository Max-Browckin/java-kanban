package model;

import com.google.gson.annotations.Expose;
import tasktype.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

public class Subtask extends Task {
    @Expose
    private final int epicID;

    public Subtask(String name, String description, int epicID) {
        super(name, description);
        this.epicID = epicID;
    }

    public Subtask(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime, int epicID) {
        super(id, name, description, status, duration, startTime);
        this.epicID = epicID;
    }

    public Subtask(String name, String description, int epicID, Duration duration, LocalDateTime startTime) {
        super(name, description, duration, startTime);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", epicID=" + epicID +
                ", status=" + getStatus() +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + " minutes" : "null") +
                ", startTime=" + (getStartTime() != null ? getStartTime().format(formatter) : "null") +
                ", endTime=" + (getEndTime() != null ? getEndTime().format(formatter) : "null") +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return epicID == subtask.epicID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(epicID);
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }
}