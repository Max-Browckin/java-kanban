package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;
import model.Status;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.HttpTaskServer;
import server.adapters.DurationAdapter;
import server.adapters.LocalDateTimeAdapter;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTest {

    private HttpTaskServer taskServer;
    private TaskManager manager;
    private Gson gson;


    @BeforeEach
    public void setUp() throws IOException {

        manager = new InMemoryTaskManager();

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();

        taskServer = new HttpTaskServer(manager);
        taskServer.start();

        gson = new GsonBuilder()
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }

    @AfterEach
    public void tearDown() {

        taskServer.stop();
    }

    @Test
    public void testAddTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        List<Task> tasks = manager.getTasks();
        assertNotNull(tasks, "Список задач не должен быть null");
        assertEquals(1, tasks.size(), "Ожидалась одна задача в списке");
        assertEquals("Test Task", tasks.get(0).getName(), "Название задачи не совпадает");
    }

    @Test
    public void testGetTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Test Description");
        task.setStatus(Status.NEW);
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        Task returnedTask = gson.fromJson(response.body(), Task.class);
        assertNotNull(returnedTask, "Задача не должна быть null");
        assertEquals(task.getName(), returnedTask.getName(), "Название задачи не совпадает");
        assertEquals(task.getDuration(), returnedTask.getDuration(), "Длительность задачи не совпадает");
        assertEquals(task.getStartTime(), returnedTask.getStartTime(), "Время начала задачи не совпадает");
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {
        Task task = new Task(1, "Test Task", "Test Description");
        task.setStatus(Status.NEW);
        manager.addTask(task);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        assertNull(manager.getTaskByID(task.getId()), "Задача должна быть удалена");
    }

    @Test
    public void testAddEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        String epicJson = gson.toJson(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        List<Epic> epics = manager.getEpics();
        assertNotNull(epics, "Список эпиков не должен быть null");
        assertEquals(1, epics.size(), "Ожидался один эпик в списке");
        assertEquals("Test Epic", epics.get(0).getName(), "Название эпика не совпадает");
    }

    @Test
    public void testGetEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        Epic returnedEpic = gson.fromJson(response.body(), Epic.class);
        assertNotNull(returnedEpic, "Эпик не должен быть null");
        assertEquals(epic.getName(), returnedEpic.getName(), "Название эпика не совпадает");
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epic.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        assertNull(manager.getEpicByID(epic.getId()), "Эпик должен быть удален");
    }

    @Test
    public void testAddSubtask() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        String subtaskJson = gson.toJson(subtask);

        // Отправляем POST-запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем ответ
        assertEquals(201, response.statusCode(), "Ожидался статус 201 (Created)");

        // Проверяем, что подзадача была добавлена
        List<Subtask> subtasks = manager.getSubtasks();
        assertNotNull(subtasks, "Список подзадач не должен быть null");
        assertEquals(1, subtasks.size(), "Ожидалась одна подзадача в списке");
        assertEquals("Test Subtask", subtasks.get(0).getName(), "Название подзадачи не совпадает");
    }

    @Test
    public void testGetSubtask() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());
        manager.addSubtask(subtask);

        // Отправляем GET-запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        // Проверяем, что подзадача была возвращена
        Subtask returnedSubtask = gson.fromJson(response.body(), Subtask.class);
        assertNotNull(returnedSubtask, "Подзадача не должна быть null");
        assertEquals(subtask.getName(), returnedSubtask.getName(), "Название подзадачи не совпадает");
        assertEquals(subtask.getDuration(), returnedSubtask.getDuration(), "Длительность подзадачи не совпадает");
        assertEquals(subtask.getStartTime(), returnedSubtask.getStartTime(), "Время начала подзадачи не совпадает");
    }

    @Test
    public void testDeleteSubtask() throws IOException, InterruptedException {
        // Создаем эпик
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        // Создаем подзадачу
        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        subtask.setStatus(Status.NEW);
        manager.addSubtask(subtask);

        // Отправляем DELETE-запрос
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subtask.getId()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Проверяем ответ
        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        // Проверяем, что подзадача была удалена
        assertNull(manager.getSubtaskByID(subtask.getId()), "Подзадача должна быть удалена");
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {
        Epic epic = new Epic("Test Epic", "Test Description");
        manager.addEpic(epic);

        Subtask subtask = new Subtask("Test Subtask", "Test Description", epic.getId());
        subtask.setStatus(Status.NEW);
        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());
        manager.addSubtask(subtask);

        subtask.setDescription("Updated Description");
        String subtaskJson = gson.toJson(subtask);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(subtaskJson))
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        Subtask updatedSubtask = manager.getSubtaskByID(subtask.getId());
        assertNotNull(updatedSubtask, "Подзадача не должна быть null");
        assertEquals("Updated Description", updatedSubtask.getDescription(), "Описание подзадачи не обновлено");
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description 1");
        Task task2 = new Task("Task 2", "Description 2");
        manager.addTask(task1);
        manager.addTask(task2);

        manager.getTaskByID(task1.getId());
        manager.getTaskByID(task2.getId());

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode(), "Ожидался статус 200 (OK)");

        List<Task> history = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(history, "История не должна быть null");
        assertEquals(2, history.size(), "Ожидалось две задачи в истории");
        assertEquals(task1.getName(), history.get(0).getName(), "Название первой задачи не совпадает");
        assertEquals(task2.getName(), history.get(1).getName(), "Название второй задачи не совпадает");
    }
}
