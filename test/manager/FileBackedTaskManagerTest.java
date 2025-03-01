package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    private File file;

    @BeforeEach
    public void setUp() {
        file = new File("test_tasks.csv");
        taskManager = new FileBackedTaskManager(file);
    }

    @AfterEach
    public void tearDown() {
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(file);
    }

    @Test
    public void testAddTaskAndCheckFileContent() throws IOException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(2, lines.size());
        assertTrue(lines.get(1).contains("Test Task"));
    }

    @Test
    public void testDeleteTaskAndCheckFileContent() throws IOException {
        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), LocalDateTime.now());
        taskManager.addTask(task);
        taskManager.deleteTaskByID(task.getId());

        List<String> lines = Files.readAllLines(file.toPath());
        assertEquals(1, lines.size());
    }

    @Test
    public void testUpdateTaskAndCheckFileContent() throws IOException {
        LocalDateTime now = LocalDateTime.now();

        Task task = new Task("Test Task", "Description", Duration.ofMinutes(30), now);
        taskManager.addTask(task);

        task.setDescription("Updated Description");
        taskManager.updateTask(task);

        List<String> lines = Files.readAllLines(file.toPath());
        assertTrue(lines.get(1).contains("Updated Description"));
    }

    @Test
    public void testLoadFromFileWithEmptyFile() throws IOException {
        file.createNewFile();

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(file);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }
}