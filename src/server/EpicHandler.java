package server;

import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import model.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class EpicHandler extends BaseHttpHandler {
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
    }

    private void handlePostRequest(HttpExchange exchange) throws IOException {
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = gson.fromJson(body, Epic.class);
        if (epic == null || epic.getName() == null || epic.getDescription() == null) {
            sendBadRequest(exchange, "Invalid epic data");
            return;
        }

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
            sendText(exchange, gson.toJson(epic), 201);
        } else {
            taskManager.updateEpic(epic);
            sendText(exchange, gson.toJson(epic), 200);
        }
    }

    private void handleDeleteRequest(HttpExchange exchange, String[] pathParts) throws IOException {
        if (pathParts.length == 2) {
            taskManager.deleteEpics();
            sendText(exchange, "All epics deleted", 200);
        } else if (pathParts.length == 3) {
            int id = Integer.parseInt(pathParts[2]);
            taskManager.deleteEpicByID(id);
            sendText(exchange, "Epic deleted", 200);
        }
    }
}