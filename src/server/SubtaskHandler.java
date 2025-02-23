package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

public class SubtaskHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(SubtaskHandler.class.getName());
    private final TaskManager taskManager;

    public SubtaskHandler(TaskManager taskManager) {
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
                    if (pathParts.length == 2) {
                        ArrayList<Subtask> subtasks = taskManager.getSubtasks();
                        sendText(exchange, gson.toJson(subtasks), 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        Subtask subtask = taskManager.getSubtaskByID(id);
                        if (subtask != null) {
                            sendText(exchange, gson.toJson(subtask), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Subtask subtask = gson.fromJson(body, Subtask.class);
                    if (subtask.getId() == 0) {
                        if (taskManager.getEpicByID(subtask.getEpicID()) == null) {
                            sendText(exchange, "Epic with ID " + subtask.getEpicID() + " does not exist", 404);
                        } else {
                            taskManager.addSubtask(subtask);
                            sendText(exchange, "Subtask added", 201);
                        }
                    } else {
                        taskManager.updateSubtask(subtask);
                        sendText(exchange, "Subtask updated", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteSubtasks();
                        sendText(exchange, "All subtasks deleted", 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteSubtaskByID(id);
                        sendText(exchange, "Subtask deleted", 200);
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