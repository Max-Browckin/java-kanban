package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @BeforeEach
    public void beforeEach() {
        taskManager = Managers.getDefault();
    }

    @Test
    public void getHistoryShouldReturnListOf10Tasks() {
        for (int i = 0; i < 10; i++) {
            taskManager.addTask(new Task("Some name " + i, "Some description " + i));
        }

        List<Task> tasks = taskManager.getTasks();
        for (Task task : tasks) {
            taskManager.getTaskByID(task.getId());
        }

        List<Task> list = taskManager.getHistory();
        assertEquals(10, list.size(), "Неверное количество элементов в истории ");
    }

    @Test
    public void getHistoryShouldReturnOldTaskAfterUpdate() {
        Task washFloor = new Task("Помыть полы", "С новым средством");
        taskManager.addTask(washFloor);
        taskManager.getTaskByID(washFloor.getId());


        Task updatedTask = new Task(washFloor.getId(), "Не забыть помыть полы",
                "Можно и без средства", Status.IN_PROGRESS);
        taskManager.updateTask(updatedTask);

        List<Task> tasks = taskManager.getHistory();
        Task oldTask = tasks.get(tasks.size() - 1);
        assertEquals(washFloor.getName(), oldTask.getName(), "В истории не сохранилась старая версия задачи");
        assertEquals(washFloor.getDescription(), oldTask.getDescription(),
                "В истории не сохранилась старая версия задачи");
    }


    @Test
    public void getHistoryShouldReturnOldSubtaskAfterUpdate() {
        Epic flatRenovation = new Epic(1, "Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(flatRenovation);
        Subtask flatRenovationSubtask3 = new Subtask("Заказать книжный шкаф", "Из темного дерева",
                flatRenovation.getId());
        taskManager.addSubtask(flatRenovationSubtask3);


        taskManager.getSubtaskByID(flatRenovationSubtask3.getId());

        // Update the subtask
        Subtask updatedSubtask = new Subtask(flatRenovationSubtask3.getId(), "Новое имя",
                "новое описание", Status.IN_PROGRESS, flatRenovation.getId());
        taskManager.updateSubtask(updatedSubtask);

        // Check the history
        List<Task> subtasks = taskManager.getHistory();
        System.out.println("History contents:");
        for (Task task : subtasks) {
            System.out.println(task.getId() + ": " + task.getName() + " (" + task.getClass().getSimpleName() + ")");
        }


        Task lastAccessedTask = subtasks.get(subtasks.size() - 1);
        if (lastAccessedTask instanceof Subtask) {
            Subtask oldSubtask = (Subtask) lastAccessedTask;
            assertEquals(flatRenovationSubtask3.getName(), oldSubtask.getName(),
                    "В истории не сохранилась старая версия подзадачи");
            assertEquals(flatRenovationSubtask3.getDescription(), oldSubtask.getDescription(),
                    "В истории не сохранилась старая версия подзадачи");
        } else {
            fail("Last accessed task is not a Subtask");
        }
    }

    @Test
    public void shouldNotRetainOldIdInDeletedSubtask() {
        Epic epic = new Epic(1, "Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Заказать книжный шкаф", "Из темного дерева", epic.getId());
        taskManager.addSubtask(subtask);

        // Добавляем подзадачу в историю
        taskManager.getSubtaskByID(subtask.getId());

        // Удаляем подзадачу
        taskManager.deleteSubtaskByID(subtask.getId());

        // Проверяем, что подзадача не осталась в менеджере
        assertNull(taskManager.getSubtaskByID(subtask.getId()), "Подзадача не была удалена");

        // Проверяем, что в истории не осталось старого ID
        List<Task> history = taskManager.getHistory();
        assertFalse(history.stream().anyMatch(task -> task.getId() == subtask.getId()),
                "История содержит неактуальный ID подзадачи");
    }

    @Test
    public void shouldNotHaveInvalidSubtaskIdsInEpic() {
        Epic epic = new Epic(1, "Сделать ремонт", "Нужно успеть за отпуск");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Заказать книжный шкаф", "Из темного дерева", epic.getId());
        taskManager.addSubtask(subtask);

        // Удаляем подзадачу
        taskManager.deleteSubtaskByID(subtask.getId());

        // Проверяем, что в эпике не осталось неактуальных ID подзадач
        List<Subtask> subtasksInEpic = taskManager.getEpicSubtasks(epic); // Передаем объект epic
        assertFalse(subtasksInEpic.stream().anyMatch(s -> s.getId() == subtask.getId()),
                "Эпик содержит неактуальный ID подзадачи");
    }


    @Test
    public void testGetTaskByIDAddsToHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        taskManager.getTaskByID(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testUpdateTaskUpdatesTaskAndAddsOldVersionToHistory() {
        InMemoryTaskManager taskManager = new InMemoryTaskManager();
        Task task = new Task("Test Task", "Description");
        taskManager.addTask(task);

        Task updatedTask = new Task(task.getId(), "Updated Task", "Updated Description");
        taskManager.updateTask(updatedTask);

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0)); // Проверяем, что старая версия добавлена в историю
    }
}