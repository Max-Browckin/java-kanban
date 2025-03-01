package model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Subtask> subtaskList = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description);
    }

    public Epic(String name, String description) {
        super(name, description);
    }

    public Epic(int id, String name, String description, Status status, Duration duration, LocalDateTime startTime) {
        super(id, name, description, status, duration, startTime);
    }

    public Epic(int id, String name, String description, Duration duration, LocalDateTime startTime) {
        super(id, name, description, Status.NEW, duration, startTime);
    }

    public void addSubtask(Subtask subtask) {
        if (subtask == null) {
            throw new IllegalArgumentException("Subtask cannot be null");
        }
        if (!subtaskList.contains(subtask)) {
            subtaskList.add(subtask);
            updateEpicDetails();
        }
    }

    public void clearSubtasks() {
        subtaskList.clear();
        updateEpicDetails();
    }

    public ArrayList<Subtask> getSubtaskList() {
        return subtaskList;
    }

    public void setSubtaskList(ArrayList<Subtask> subtaskList) {
        if (subtaskList == null) {
            throw new IllegalArgumentException("Subtask list cannot be null");
        }
        this.subtaskList = subtaskList;
        updateEpicDetails();
    }

    private void updateEpicDetails() {
        if (subtaskList.isEmpty()) {
            this.setStatus(Status.NEW);
            this.duration = Duration.ZERO;
            this.startTime = null;
            return;
        }

        LocalDateTime earliestStart = null;
        LocalDateTime latestEnd = null;
        Duration totalDuration = Duration.ZERO;
        int doneCount = 0;
        int newCount = 0;

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
                totalDuration = totalDuration.plus(subtask.getDuration());
            }

            if (subtask.getStatus() == Status.DONE) {
                doneCount++;
            } else if (subtask.getStatus() == Status.NEW) {
                newCount++;
            }
        }

        this.startTime = earliestStart;
        this.duration = totalDuration;

        if (doneCount == subtaskList.size()) {
            this.setStatus(Status.DONE);
        } else if (newCount == subtaskList.size()) {
            this.setStatus(Status.NEW);
        } else {
            this.setStatus(Status.IN_PROGRESS);
        }
    }

    @Override
    public String toString() {
        return "Epic{" +
                "name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", id=" + getId() +
                ", subtaskList=" + subtaskList +
                ", status=" + getStatus() +
                ", duration=" + (getDuration() != null ? getDuration().toMinutes() + " minutes" : "null") +
                ", startTime=" + (getStartTime() != null ? getStartTime().format(formatter) : "null") +
                ", endTime=" + (getEndTime() != null ? getEndTime().format(formatter) : "null") +
                '}';
    }
}