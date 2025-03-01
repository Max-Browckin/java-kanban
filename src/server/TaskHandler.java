package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class TaskHandler extends BaseHttpHandler {
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

            switch (method) {
                case "GET":
                    handleGetRequest(exchange, pathParts);
                    break;
                case "POST":
                    handlePostRequest(exchange);
                    break;
                case "DELETE":
                    handleDeleteRequest(exchange, pathParts);
                    break;
                default:
                    sendNotFound(exchange);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendInternalError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] pathParts) throws IOException {
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
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = gson.fromJson(body, Task.class);
        if (task.getId() == 0) {
            try {
                taskManager.addTask(task);
                sendText(exchange, gson.toJson(task), 201);
            } catch (IllegalArgumentException e) {
                sendHasInteractions(exchange);
            }
        } else {
            taskManager.updateTask(task);
            sendText(exchange, gson.toJson(task), 200);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            taskManager.deleteTasks();
            sendText(exchange, "All tasks deleted", 200);
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            taskManager.deleteTaskByID(id);
            sendText(exchange, "Task deleted", 200);
        }
    }
}