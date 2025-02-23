import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

public class Main {

    private static final TaskManager taskManager = Managers.getDefault();

    public static void main(String[] args) {
        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        Task packBoxes = new Task("Упаковать вещи", "В коробки и мешки");
        taskManager.addTask(packBoxes);

        Task packBoxesToUpdate = new Task(packBoxes.getId(),  "Упаковать вещи быстро", "В коробки и мешки",
                Status.IN_PROGRESS);
        taskManager.updateTask(packBoxesToUpdate);
        taskManager.addTask(new Task("Придумать список дел после перезда", "Список в заметках"));

        Epic moving = new Epic(10, "Переезд", "Нужно успеть до конца месяца");
        taskManager.addEpic(moving);
        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки",
                moving.getId());
        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки",
                moving.getId());
        taskManager.addSubtask(packKitchen);
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