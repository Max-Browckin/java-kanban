package manager;


import model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.*;

class InMemoryTaskManagerTest {
    private InMemoryTaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new InMemoryTaskManager();
    }

    @Test
    void testAddTask() {
        Task task = new Task("Test Task", "This is a test task");
        Task addedTask = taskManager.addTask(task);
        assertNotNull(addedTask);
        assertEquals("Test Task", addedTask.getName());
        assertEquals(1, addedTask.getId());
    }

    @Test
    void testUpdateTask() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addTask(task);
        task.setName("Updated Task");
        Task updatedTask = taskManager.updateTask(task);
        assertNotNull(updatedTask);
        assertEquals("Updated Task", updatedTask.getName());
    }

    @Test
    void testGetTaskByID() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addTask(task);
        Task retrievedTask = taskManager.getTaskByID(task.getId());
        assertNotNull(retrievedTask);
        assertEquals(task.getId(), retrievedTask.getId());
    }

    @Test
    void testDeleteTaskByID() {
        Task task = new Task("Test Task", "This is a test task");
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getId());
        assertNull(taskManager.getTaskByID(task.getId()));
    }

    @Test
    void testDeleteTasks() {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        taskManager.addTask(task1);
        taskManager.addTask(task2);
        taskManager.deleteTasks();
        assertTrue(taskManager.getTasks().isEmpty());
    }


}