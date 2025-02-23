package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

public class TaskHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(TaskHandler.class.getName());
    private final TaskManager taskManager;

    public TaskHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] pathParts = path.split("/");

            logger.info("Received request: " + method + " " + path);

            switch (method) {
                case "GET":
                    if (pathParts.length == 2) {
                        ArrayList<Task> tasks = taskManager.getTasks();
                        sendText(exchange, gson.toJson(tasks), 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        Task task = taskManager.getTaskByID(id);
                        if (task != null) {
                            sendText(exchange, gson.toJson(task), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    logger.info("Request body: " + body); // Логируем тело запроса
                    Task task = gson.fromJson(body, Task.class);
                    if (task.getName() == null || task.getName().isEmpty()) {
                        sendText(exchange, "Task name cannot be null or empty", 400);
                    } else if (task.getId() == 0) {
                        taskManager.addTask(task);
                        sendText(exchange, "Task added", 201);
                    } else {
                        taskManager.updateTask(task);
                        sendText(exchange, "Task updated", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteTasks();
                        sendText(exchange, "All tasks deleted", 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteTaskByID(id);
                        sendText(exchange, "Task deleted", 200);
                    }
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (NumberFormatException e) {
            logger.severe("Invalid ID format: " + e.getMessage());
            sendText(exchange, "Invalid ID format", 400);
        } catch (Exception e) {
            logger.severe("Error handling request: " + e.getMessage());
            sendInternalError(exchange);
        }
    }
}