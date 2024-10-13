import manager.InMemoryTaskManager;
import manager.Managers;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;


public class Main {

    private static final InMemoryTaskManager inMemoryTaskManager = (InMemoryTaskManager) Managers.getDefault();

    public static void main(String[] args) {

        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        Task packBoxes = new Task("Упаковать вещи", "В коробки и мешки");
        inMemoryTaskManager.addTask(packBoxes);

        Task packBoxesToUpdate = new Task(packBoxes.getId(),  "Упаковать вещи быстро", "В коробки и мешки",
                Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(packBoxesToUpdate);
        inMemoryTaskManager.addTask(new Task("Придумать список дел после перезда", "Список в заметках"));


        Epic moving = new Epic("Переезд", "Нужно успеть до конца месяца");
        inMemoryTaskManager.addEpic(moving);
        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки",
                moving.getId());
        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки",
                moving.getId());
        inMemoryTaskManager.addSubtask(packKitchen);
        inMemoryTaskManager.addSubtask(packBedroom);
        packBedroom.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(packBedroom);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : Main.inMemoryTaskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : Main.inMemoryTaskManager.getEpics()) {
            System.out.println(epic);

            for (Task task : Main.inMemoryTaskManager.getEpicSubtasks(epic)) {
                System.out.println("--> " + task);
            }
        }

        System.out.println("Подзадачи:");
        for (Task subtask : Main.inMemoryTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printViewHistory() {
        //просматриваем 11 задач, в истории должны отобразиться последние 10
        Main.inMemoryTaskManager.getTaskByID(1);
        Main.inMemoryTaskManager.getTaskByID(2);
        Main.inMemoryTaskManager.getEpicByID(3);
        Main.inMemoryTaskManager.getTaskByID(1);
        Main.inMemoryTaskManager.getSubtaskByID(4);
        Main.inMemoryTaskManager.getSubtaskByID(5);
        Main.inMemoryTaskManager.getSubtaskByID(6);
        Main.inMemoryTaskManager.getEpicByID(3);
        Main.inMemoryTaskManager.getSubtaskByID(4);
        Main.inMemoryTaskManager.getTaskByID(2);
        Main.inMemoryTaskManager.getSubtaskByID(6);

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : Main.inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
    }
}