package model;

import java.util.Objects;

public class Task {
    private String name;
    private String description;
    private int id;
    private Status status;

    // Constructor with parameters
    public Task(int id, String name, String description, Status status) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    // Constructor with parameters (default status NEW)
    public Task(String name, String description) {
        this.name = name;
        this.description = description;
        this.status = Status.NEW;
    }

    // Constructor with parameters (default status NEW)
    public Task(int id, String name, String description) {
        this(id, name, description, Status.NEW);
    }

    // Copy constructor
    public Task(Task task) {
        this.id = task.id; // Copy id
        this.name = task.name; // Copy name
        this.description = task.description; // Copy description
        this.status = task.status; // Copy status
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Task task = (Task) object;
        return id == task.id; // Compare by id for equality
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Hash based on id only for uniqueness
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", id=" + id +
                ", status=" + status +
                '}';
    }
}