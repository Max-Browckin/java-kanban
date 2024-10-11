import controllers.*;
import model.*;

public class Main {

    public static void main(String[] args) {

        TaskManager taskManager = new TaskManager();

        Task packBoxes = new Task("Упаковать вещи", "В коробки и мешки");
        Task packBoxesCreated = taskManager.addTask(packBoxes);
        System.out.println(packBoxesCreated);

        Task packBoxesToUpdate = new Task(packBoxes.getId(), "Упаковать вещи быстро", "В коробки и мешки",
                Status.IN_PROGRESS);
        Task packBoxesUpdated = taskManager.updateTask(packBoxesToUpdate);
        System.out.println(packBoxesUpdated);

        Epic moving = new Epic("Переезд", "Нужно успеть до конца месяца");
        taskManager.addEpic(moving);
        System.out.println(moving);
        Subtask packKitchen = new Subtask("Упаковать кухню", "В отдельные коробки",
                moving.getId());
        Subtask packBedroom = new Subtask("Упаковать спальню", "В большие коробки",
                moving.getId());
        taskManager.addSubtask(packKitchen);
        taskManager.addSubtask(packBedroom);
        System.out.println(moving);
        packBedroom.setStatus(Status.DONE);
        taskManager.updateSubtask(packBedroom);
        packKitchen.setStatus(Status.DONE);
        taskManager.updateSubtask(packKitchen);
        System.out.println(moving);
    }
}