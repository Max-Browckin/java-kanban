package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.logging.Logger;

public class EpicHandler extends BaseHttpHandler {
    private static final Logger logger = Logger.getLogger(EpicHandler.class.getName());
    private final TaskManager taskManager;

    public EpicHandler(TaskManager taskManager) {
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
                        ArrayList<Epic> epics = taskManager.getEpics();
                        sendText(exchange, gson.toJson(epics), 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        Epic epic = taskManager.getEpicByID(id);
                        if (epic != null) {
                            sendText(exchange, gson.toJson(epic), 200);
                        } else {
                            sendNotFound(exchange);
                        }
                    }
                    break;
                case "POST":
                    String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Epic epic = gson.fromJson(body, Epic.class);
                    if (epic.getId() == 0) {
                        taskManager.addEpic(epic);
                        sendText(exchange, "Epic added", 201);
                    } else {
                        taskManager.updateEpic(epic);
                        sendText(exchange, "Epic updated", 201);
                    }
                    break;
                case "DELETE":
                    if (pathParts.length == 2) {
                        taskManager.deleteEpics();
                        sendText(exchange, "All epics deleted", 200);
                    } else if (pathParts.length == 3) {
                        int id = Integer.parseInt(pathParts[2]);
                        taskManager.deleteEpicByID(id);
                        sendText(exchange, "Epic deleted", 200);
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