package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
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


    public void save() {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("id,type,name,status,description,epic");
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
        if (task instanceof Subtask) {
            return String.format("%d,SUBTASK,%s,%s,%s,%d",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription(), ((Subtask) task).getEpicID());
        } else if (task instanceof Epic) {
            return String.format("%d,EPIC,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        } else {
            return String.format("%d,TASK,%s,%s,%s,",
                    task.getId(), task.getName(), task.getStatus(), task.getDescription());
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            for (String line : lines.subList(1, lines.size())) {
                Task task = fromString(line);
                if (task instanceof Epic) {
                    manager.addEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.addSubtask((Subtask) task);
                } else {
                    manager.addTask(task);
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
        if (type.equals("SUBTASK")) {
            int epicID = Integer.parseInt(parts[5]);
            return new Subtask(id, name, description, status, epicID);
        } else if (type.equals("EPIC")) {
            return new Epic(id, name, description);
        } else {
            return new Task(id, name, description, status);
        }
    }
}