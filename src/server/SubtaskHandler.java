package server;

import com.google.gson.JsonSyntaxException;
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

            logger.info("Received " + method + " request for path: " + path);

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
            logger.severe("Error handling request: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleGetRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            ArrayList<Subtask> subtasks = taskManager.getSubtasks();
            logger.info("Returning all subtasks: " + subtasks.size());
            sendText(exchange, gson.toJson(subtasks), 200);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                Subtask subtask = taskManager.getSubtaskByID(id);
                if (subtask != null) {
                    logger.info("Returning subtask with ID: " + id);
                    sendText(exchange, gson.toJson(subtask), 200);
                } else {
                    logger.warning("Subtask not found with ID: " + id);
                    sendNotFound(exchange, "Subtask not found with ID: " + id);
                }
            } catch (NumberFormatException e) {
                logger.warning("Invalid subtask ID format: " + pathParts[2]);
                sendBadRequest(exchange, "Invalid subtask ID format");
            }
        }
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        logger.info("Received POST request with body: " + body);

        try {
            Subtask subtask = gson.fromJson(body, Subtask.class);
            if (subtask == null) {
                logger.warning("Invalid subtask data received");
                sendBadRequest(exchange, "Invalid subtask data");
                return;
            }

            if (subtask.getId() == 0) {
                if (taskManager.getEpicByID(subtask.getEpicID()) == null) {
                    logger.warning("Epic not found with ID: " + subtask.getEpicID());
                    sendNotFound(exchange, "Epic not found with ID: " + subtask.getEpicID());
                    return;
                }
                try {
                    taskManager.addSubtask(subtask);
                    logger.info("Created new subtask with ID: " + subtask.getId());
                    sendText(exchange, gson.toJson(subtask), 201);
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            } else {
                if (taskManager.getSubtaskByID(subtask.getId()) == null) {
                    logger.warning("Subtask not found with ID: " + subtask.getId());
                    sendNotFound(exchange, "Subtask not found with ID: " + subtask.getId());
                    return;
                }
                taskManager.updateSubtask(subtask);
                logger.info("Updated subtask with ID: " + subtask.getId());
                sendText(exchange, gson.toJson(subtask), 200);
            }
        } catch (JsonSyntaxException e) {
            logger.severe("Error parsing subtask data: " + e.getMessage());
            sendBadRequest(exchange, "Invalid JSON data");
        } catch (Exception e) {
            logger.severe("Error handling request: " + e.getMessage());
            sendInternalError(exchange);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            taskManager.deleteSubtasks();
            logger.info("All subtasks deleted");
            sendText(exchange, "All subtasks deleted", 200);
        } else if (pathParts.length == 3) {
            try {
                int id = Integer.parseInt(pathParts[2]);
                taskManager.deleteSubtaskByID(id);
                logger.info("Deleted subtask with ID: " + id);
                sendText(exchange, "Subtask deleted", 200);
            } catch (NumberFormatException e) {
                logger.warning("Invalid subtask ID format: " + pathParts[2]);
                sendBadRequest(exchange, "Invalid subtask ID format");
            }
        }
    }
}