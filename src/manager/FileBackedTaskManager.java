package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import tasktype.TaskType;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task addTask(Task task) {
        Task addedTask = super.addTask(task);
        save();
        return addedTask;
    }

    @Override
    public Epic addEpic(Epic epic) {
        Epic addedEpic = super.addEpic(epic);
        save();
        return addedEpic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Subtask addedSubtask = super.addSubtask(subtask);
        save();
        return addedSubtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        Task existingTask = super.updateTask(updatedTask);
        save();
        return existingTask;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        Epic existingEpic = super.updateEpic(updatedEpic);
        save();
        return existingEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask updatedSubtask) {
        Subtask existingSubtask = super.updateSubtask(updatedSubtask);
        save();
        return existingSubtask;
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int id) {
        super.deleteSubtaskByID(id);
        save();
    }

    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    public void save() {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("id,type,name,status,description,epic,duration,startTime");
            for (Task task : getTasks()) {
                writer.println(toString(task));
            }
            for (Epic epic : getEpics()) {
                writer.println(toString(epic));
            }
            for (Subtask subtask : getSubtasks()) {
                writer.println(toString(subtask));
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to save tasks to file", e);
        }
    }

    private String toString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getId()).append(",")
                .append(task.getType()).append(",")
                .append(task.getName()).append(",")
                .append(task.getStatus()).append(",")
                .append(task.getDescription()).append(",");

        if (task instanceof Subtask) {
            sb.append(((Subtask) task).getEpicID()).append(",");
        } else {
            sb.append(",");
        }

        sb.append(task.getDuration() != null ? task.getDuration().toMinutes() : 0).append(",")
                .append(task.getStartTime() != null ? task.getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "");

        return sb.toString();
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() > 1) {
                for (String line : lines.subList(1, lines.size())) {
                    Task task = fromString(line);
                    if (task instanceof Epic) {
                        manager.addEpic((Epic) task);
                        System.out.println("Loaded epic: " + task);
                    }
                }

                for (String line : lines.subList(1, lines.size())) {
                    Task task = fromString(line);
                    if (task instanceof Subtask) {
                        manager.addSubtask((Subtask) task);
                        System.out.println("Loaded subtask: " + task);
                    } else if (task instanceof Task && !(task instanceof Epic)) {
                        manager.addTask(task);
                        System.out.println("Loaded task: " + task);
                    }
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Failed to load tasks from file", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        TaskType taskType = TaskType.valueOf(type);
        Duration duration = Duration.ofMinutes(Long.parseLong(parts[6]));
        LocalDateTime startTime = parts.length > 7 && !parts[7].isEmpty() ? LocalDateTime.parse(parts[7]) : null;

        switch (taskType) {
            case SUBTASK:
                int epicID = Integer.parseInt(parts[5]);
                return new Subtask(id, name, description, status, duration, startTime, epicID);
            case EPIC:
                return new Epic(id, name, description, status, duration, startTime);
            case TASK:
            default:
                return new Task(id, name, description, status, duration, startTime);
        }
    }
}