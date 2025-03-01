package manager;

import model.Epic;
import model.Status;
import model.Task;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    @Test
    public void testEmptyHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size(), "History should be empty initially.");
    }

    @Test
    public void testDuplicateTask() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());


        historyManager.add(task);
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size(), "History should contain only one instance of the task.");
        assertEquals(task, history.get(0), "The task in history should be the same as the added task.");
    }

    @Test
    public void testRemoveFromBeginning() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task3 = new Task(3, "Task3", "Description3", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain two tasks after removing the first one.");
        assertEquals(task2, history.get(0), "The first task in history should now be Task2.");
        assertEquals(task3, history.get(1), "The second task in history should still be Task3.");
    }

    @Test
    public void testRemoveFromMiddle() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task3 = new Task(3, "Task3", "Description3", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain two tasks after removing the second one.");
        assertEquals(task1, history.get(0), "The first task in history should still be Task1.");
        assertEquals(task3, history.get(1), "The second task in history should now be Task3.");
    }

    @Test
    public void testRemoveFromEnd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task3 = new Task(3, "Task3", "Description3", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());

        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        historyManager.remove(3);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size(), "History should contain two tasks after removing the last one.");
        assertEquals(task1, history.get(0), "The first task in history should still be Task1.");
        assertEquals(task2, history.get(1), "The second task in history should now be Task2.");
    }
}