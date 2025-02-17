package model;

import tasktype.TaskType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }
    public Epic(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public Epic(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, Status.NEW, duration, startTime);
    }

    public void addSubtask(Subtask subtask) {
        subtaskList.add(subtask);
        updateEpicDetails();
    }

    public void clearSubtasks() {
        subtaskList.clear();
        updateEpicDetails();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        this.subtaskList = subtaskList;
        updateEpicDetails();
    }

    private void updateEpicDetails() {
        this.duration = Duration.ZERO;
        this.startTime = null;
        this.endTime = null;

        if (!subtaskList.isEmpty()) {
            LocalDateTime earliestStart = null;
            LocalDateTime latestEnd = null;

            for (Subtask subtask : subtaskList) {
                if (subtask.getStartTime() != null) {
                    if (earliestStart == null || subtask.getStartTime().isBefore(earliestStart)) {
                        earliestStart = subtask.getStartTime();
                    }
                    if (latestEnd == null || subtask.getEndTime().isAfter(latestEnd)) {
                        latestEnd = subtask.getEndTime();
                    }
                }
                if (subtask.getDuration() != null) {
                    this.duration = this.duration.plus(subtask.getDuration());
                }
            }

            this.startTime = earliestStart;
            this.endTime = latestEnd;
        }
    }

    public Duration getDuration() {
        return duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList.size=" + subtaskList.size() +
                ", status=" + getStatus() +
                ", duration=" + duration +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Epic other = (Epic) obj;
        return getId() == other.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public TaskType getType() {
        return TaskType.EPIC;
    }
}