package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();
    }

    protected abstract T createTaskManager();

    @Test
    public void testAddTask() {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        assertEquals(task, taskManager.getTaskByID(task.getId()));
    }

    @Test
    public void testAddEpic() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        assertEquals(epic, taskManager.getEpicByID(epic.getId()));
    }

    @Test
    public void testAddSubtask() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addSubtask(subtask);
        assertEquals(subtask, taskManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void testEpicStatusCalculation() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask1 = new Subtask("Subtask 1", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        Subtask subtask2 = new Subtask("Subtask 2", "Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addSubtask(subtask1);
        taskManager.addSubtask(subtask2);

        assertEquals(Status.NEW, epic.getStatus());

        subtask1.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask1);

        assertEquals(Status.IN_PROGRESS, epic.getStatus());

        subtask2.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask2);

        assertEquals(Status.DONE, epic.getStatus());
    }

    @Test
    public void testGetPrioritizedTasks() {
        LocalDateTime now = LocalDateTime.now();

        Task task1 = new Task("Task 1", "Description 1", Duration.ofMinutes(30), now.plusMinutes(10));
        Task task2 = new Task("Task 2", "Description 2", Duration.ofMinutes(30), now.plusMinutes(50));
        Task task3 = new Task("Task 3", "Description 3", Duration.ofMinutes(30), now.plusMinutes(90));

        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.addTask(task3);

        List<Task> prioritizedTasks = taskManager.getPrioritizedTasks();

        assertEquals(3, prioritizedTasks.size());
        assertEquals(task1, prioritizedTasks.get(0));
        assertEquals(task2, prioritizedTasks.get(1));
        assertEquals(task3, prioritizedTasks.get(2));
    }

    @Test
    public void testDeleteTaskByID() {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getId());
        assertNull(taskManager.getTaskByID(task.getId()));
    }

    @Test
    public void testDeleteEpicWithSubtasks() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addSubtask(subtask);
        taskManager.deleteEpicByID(epic.getId());
        assertNull(taskManager.getEpicByID(epic.getId()));
        assertNull(taskManager.getSubtaskByID(subtask.getId()));
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        task.setDescription("Updated Description");
        taskManager.updateTask(task);
        assertEquals("Updated Description", taskManager.getTaskByID(task.getId()).getDescription());
    }

    @Test
    public void testUpdateEpic() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        epic.setDescription("Updated Epic Description");
        taskManager.updateEpic(epic);
        assertEquals("Updated Epic Description", taskManager.getEpicByID(epic.getId()).getDescription());
    }

    @Test
    public void testUpdateSubtask() {
        Epic epic = new Epic(10, "Test Epic", "Epic Description");
        taskManager.addEpic(epic);
        Subtask subtask = new Subtask("Test Subtask", "Subtask Description", epic.getId(), Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addSubtask(subtask);
        subtask.setDescription("Updated Subtask Description");
        taskManager.updateSubtask(subtask);
        assertEquals("Updated Subtask Description", taskManager.getSubtaskByID(subtask.getId()).getDescription());
    }
}