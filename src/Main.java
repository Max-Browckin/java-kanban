import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    private static final TaskManager taskManager = Managers.getDefault();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task packBoxes = new Task("Упаковать вещи", "В коробки и мешки", Duration.ofMinutes(30), now);
        taskManager.addTask(packBoxes);

        Task packBoxesToUpdate = new Task(packBoxes.getId(), "Упаковать вещи быстро", "В коробки и мешки",
                Status.IN_PROGRESS, Duration.ofMinutes(20), now.plusMinutes(30));
        taskManager.updateTask(packBoxesToUpdate);

        Task createToDoList = new Task("Придумать список дел после перезда", "Список в заметках", Duration.ofMinutes(15), now.plusMinutes(50));
        taskManager.addTask(createToDoList);

        Epic moving = new Epic(10, "Переезд", "Нужно успеть до конца месяца");
        taskManager.addEpic(moving);

        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки", moving.getId(), Duration.ofMinutes(30), now.plusMinutes(10));
        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки", moving.getId(), Duration.ofMinutes(45), now.plusMinutes(40));

        taskManager.addSubtask(packKitchen);
        taskManager.addSubtask(packBedroom);

        packBedroom.setStatus(Status.DONE);
        taskManager.updateSubtask(packBedroom);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(formatTask(task));
        }

        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(formatEpic(epic));
            for (Subtask subtask : taskManager.getEpicSubtasks(epic)) {
                System.out.println("--> " + formatSubtask(subtask));
            }
        }

        System.out.println("Подзадачи:");
        for (Subtask subtask : taskManager.getSubtasks()) {
            System.out.println(formatSubtask(subtask));
        }
    }

    private static String formatTask(Task task) {
        String startTime = task.getStartTime() != null ? task.getStartTime().format(formatter) : "не задано";
        return String.format("Task{id=%d, name='%s', description='%s', status=%s, duration=%s, startTime=%s}",
                task.getId(), task.getName(), task.getDescription(), task.getStatus(), task.getDuration(), startTime);
    }

    private static String formatSubtask(Subtask subtask) {
        String startTime = subtask.getStartTime() != null ? subtask.getStartTime().format(formatter) : "не задано";
        return String.format("Subtask{name='%s', description='%s', id=%d, epicID=%d, status=%s, duration=%s, startTime=%s}",
                subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getEpicID(), subtask.getStatus(), subtask.getDuration(), startTime);
    }

    private static String formatEpic(Epic epic) {
        String startTime = epic.getStartTime() != null ? epic.getStartTime().format(formatter) : "не задано";
        return String.format("Epic{name='%s', description='%s', id=%d, subtaskList.size=%d, status=%s, duration=%s, startTime=%s}",
                epic.getName(), epic.getDescription(), epic.getId(), epic.getSubtaskList().size(), epic.getStatus(), epic.getDuration(), startTime);
    }

    private static void printViewHistory() {
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getTaskByID(1);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(4);
        taskManager.getTaskByID(2);
        taskManager.getSubtaskByID(5);

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(formatTask(task));
        }
    }
}