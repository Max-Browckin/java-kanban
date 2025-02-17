package manager;


import org.junit.jupiter.api.Test;
import model.Epic;
import model.Status;
import model.Task;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class InMemoryHistoryManagerTest {

    @Test
    public void testAdd() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    @Test
    public void testRemove() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task);
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void testGetHistory() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();
        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    public void testGetHistoryEmpty() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void testRemoveNonExisting() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        historyManager.remove(1);
        List<Task> history = historyManager.getHistory();
        assertEquals(0, history.size());
    }

    @Test
    public void testAddMultiple() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Task task1 = new Task(1, "Task1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task2 = new Task(2, "Task2", "Description2", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        Task task3 = new Task(3, "Task3", "Description3", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);
        List<Task> history = historyManager.getHistory();
        assertEquals(3, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
        assertEquals(task3, history.get(2));
    }

    @Test
    public void testAddEpic() {
        InMemoryHistoryManager historyManager = new InMemoryHistoryManager();
        Epic epic = new Epic(1, "Epic1", "Description1", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        historyManager.add(epic);
        List<Task> history = historyManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(epic, history.get(0));
    }
}