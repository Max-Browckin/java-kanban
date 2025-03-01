import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;

public class Main {

    private static final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        Task packBoxes = new Task("Упаковать вещи", "В коробки и мешки", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(packBoxes);

        Task packBoxesToUpdate = new Task(packBoxes.getId(), "Упаковать вещи быстро", "В коробки и мешки",
                Status.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.updateTask(packBoxesToUpdate);

        Task planTasks = new Task("Придумать список дел после переезда", "Список в заметках", Duration.ofMinutes(60), LocalDateTime.now().plusHours(1));
        taskManager.addTask(planTasks);

        Epic moving = new Epic("Переезд", "Нужно успеть до конца месяца");
        taskManager.addEpic(moving);

        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки", moving.getId(),
                Duration.ofMinutes(120), LocalDateTime.now().plusHours(2));
        taskManager.addSubtask(packKitchen);

        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки", moving.getId(),
                Duration.ofMinutes(90), LocalDateTime.now().plusHours(3));
        taskManager.addSubtask(packBedroom);

        packBedroom.setStatus(Status.DONE);
        taskManager.updateSubtask(packBedroom);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : taskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : taskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : taskManager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : taskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printViewHistory() {
        taskManager.getTaskByID(1);
        taskManager.getTaskByID(2);
        taskManager.getEpicByID(3);
        taskManager.getTaskByID(1);
        taskManager.getSubtaskByID(4);
        taskManager.getSubtaskByID(5);
        taskManager.getSubtaskByID(6);
        taskManager.getEpicByID(3);
        taskManager.getSubtaskByID(4);
        taskManager.getTaskByID(2);
        taskManager.getSubtaskByID(6);

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : taskManager.getHistory()) {
            System.out.println(task);
        }
    }
}