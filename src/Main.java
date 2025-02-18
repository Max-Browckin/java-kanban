import manager.Managers;
import manager.TaskManager;
import model.Epic;
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

        Task createToDoList = new Task("Придумать список дел после перезда", "Список в заметках", Duration.ofMinutes(15), now.plusMinutes(50));
        taskManager.addTask(createToDoList);

        Epic moving = new Epic(10, "Переезд", "Нужно успеть до конца месяца");
        taskManager.addEpic(moving);

        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки", moving.getId(), Duration.ofMinutes(30), now.plusMinutes(65));
        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки", moving.getId(), Duration.ofMinutes(30), now.plusMinutes(95));

        taskManager.addSubtask(packKitchen);
        taskManager.addSubtask(packBedroom);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        taskManager.getTasks().stream()
                .map(Main::formatTask)
                .forEach(System.out::println);

        System.out.println("Эпики:");
        taskManager.getEpics().forEach(epic -> {
            System.out.println(formatEpic(epic));
            taskManager.getEpicSubtasks(epic).stream()
                    .map(Main::formatSubtask)
                    .forEach(subtask -> System.out.println("--> " + subtask));
        });

        System.out.println("Подзадачи:");
        taskManager.getSubtasks().stream()
                .map(Main::formatSubtask)
                .forEach(System.out::println);
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
        taskManager.getEpicByID(10);
        taskManager.getTaskByID(1);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(5);
        taskManager.getEpicByID(10);
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