package manager;

import model.Epic;
import model.Subtask;
import model.Task;
import org.junit.jupiter.api.Test;


import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class FileBackedTasksManagerTest {
    @Test
    public void saveAndLoadEmptyFile() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        manager.save();
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty());
        assertTrue(loadedManager.getEpics().isEmpty());
        assertTrue(loadedManager.getSubtasks().isEmpty());
    }

    @Test
    public void saveAndLoadMultipleTasks() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);


        manager.addTask(new Task("Task 1", "Description 1"));
        Epic epic = new Epic(1, "Epic 1", "Description Epic 1");
        manager.addEpic(epic);
        manager.addSubtask(new Subtask("Subtask 1", "Description Subtask 1", epic.getId()));


        manager.save();


        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);


        assertEquals(1, loadedManager.getTasks().size());
        assertEquals(1, loadedManager.getEpics().size());
        assertEquals(1, loadedManager.getSubtasks().size());
    }

    @Test
    public void deleteTaskShouldRemoveTaskFromFile() throws IOException {
        File tempFile = File.createTempFile("test", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Task task = new Task("Test Task", "Description");
        manager.addTask(task);
        manager.deleteTaskByID(task.getId());

        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(tempFile);
        assertTrue(loadedManager.getTasks().isEmpty(), "Задача не была удалена из файла.");
    }
}
