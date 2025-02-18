package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.ZoneOffset;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager;
    private final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime, Comparator.nullsLast(Comparator.naturalOrder())));
    private int nextID = 1;

    public InMemoryTaskManager() {
        this.historyManager = Managers.getDefaultHistory();
    }

    private int getNextID() {
        return nextID++;
    }

    @Override
    public Task addTask(Task task) {
        validateTask(task);
        if (isOverlapping(task)) {
            throw new IllegalArgumentException("Task overlaps with existing task");
        }
        task.setId(getNextID());
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getNextID());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicID());
        if (epic == null) {
            throw new IllegalArgumentException("Epic with ID " + subtask.getEpicID() + " does not exist.");
        }
        validateTask(subtask);

        subtask.setId(getNextID());

        while (isOverlapping(subtask)) {
            subtask.setStartTime(subtask.getStartTime().plusMinutes(10));
        }

        epic.addSubtask(subtask);
        subtasks.put(subtask.getId(), subtask);
        prioritizedTasks.add(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task updateTask(Task updatedTask) {
        validateTask(updatedTask);
        Task existingTask = getTaskByID(updatedTask.getId());
        if (existingTask != null) {
            tasks.remove(existingTask.getId());
            prioritizedTasks.remove(existingTask);
            if (isOverlapping(updatedTask)) {
                tasks.put(existingTask.getId(), existingTask);
                prioritizedTasks.add(existingTask);
                throw new IllegalArgumentException("Task overlaps with existing task");
            }
            tasks.put(updatedTask.getId(), updatedTask);
            prioritizedTasks.add(updatedTask);
        }
        return existingTask;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        Integer epicID = epic.getId();
        if (epicID == null || !epics.containsKey(epicID)) {
            return null;
        }
        epics.put(epicID, epic);
        updateEpicStatus(epic);
        return epic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskID = subtask.getId();
        if (subtaskID == null || !subtasks.containsKey(subtaskID)) {
            return null;
        }
        int epicID = subtask.getEpicID();
        Epic epic = epics.get(epicID);
        Subtask oldSubtask = subtasks.get(subtaskID);

        epic.getSubtaskList().remove(oldSubtask);
        subtasks.remove(subtaskID);
        prioritizedTasks.remove(oldSubtask);

        if (isOverlapping(subtask)) {
            epic.getSubtaskList().add(oldSubtask);
            subtasks.put(subtaskID, oldSubtask);
            prioritizedTasks.add(oldSubtask);
            throw new IllegalArgumentException("Subtask overlaps with existing task");
        }

        subtasks.put(subtaskID, subtask);
        prioritizedTasks.add(subtask);
        epic.addSubtask(subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    @Override
    public Task getTaskByID(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(Epic epic) {
        return epic.getSubtaskList();
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
        prioritizedTasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        prioritizedTasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        Task task = tasks.remove(id);
        if (task != null) {
            prioritizedTasks.remove(task);
        }
    }

    @Override
    public void deleteEpicByID(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            ArrayList<Subtask> epicSubtasks = epic.getSubtaskList();
            epics.remove(id);
            for (Subtask subtask : epicSubtasks) {
                subtasks.remove(subtask.getId());
                prioritizedTasks.remove(subtask);
            }
        }
    }

    @Override
    public void deleteSubtaskByID(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            int epicID = subtask.getEpicID();
            subtasks.remove(id);
            prioritizedTasks.remove(subtask);
            historyManager.remove(id);
            Epic epic = epics.get(epicID);
            if (epic != null) {
                epic.getSubtaskList().remove(subtask);
                updateEpicStatus(epic);
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }

    private boolean isOverlapping(Task newTask) {
        for (Task existingTask : tasks.values()) {
            if (existingTask.getId() != newTask.getId() && isOverlapping(existingTask, newTask)) {
                return true;
            }
        }
        for (Subtask existingSubtask : subtasks.values()) {
            if (existingSubtask.getId() != newTask.getId() && isOverlapping(existingSubtask, newTask)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOverlapping(Task t1, Task t2) {
        if (t1.getStartTime() == null || t2.getStartTime() == null) {
            return false;
        }
        long t1StartEpoch = t1.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long t1EndEpoch = t1StartEpoch + t1.getDuration().getSeconds();
        long t2StartEpoch = t2.getStartTime().toEpochSecond(ZoneOffset.UTC);
        long t2EndEpoch = t2StartEpoch + t2.getDuration().getSeconds();

        return t1StartEpoch < t2EndEpoch && t2StartEpoch < t1EndEpoch;
    }

    private void updateEpicStatus(Epic epic) {
        int allIsDoneCount = 0;
        int allIsInNewCount = 0;
        ArrayList<Subtask> list = epic.getSubtaskList();

        for (Subtask subtask : list) {
            if (subtask.getStatus() == Status.DONE) {
                allIsDoneCount++;
            }
            if (subtask.getStatus() == Status.NEW) {
                allIsInNewCount++;
            }
        }
        if (list.isEmpty() || allIsDoneCount == list.size()) {
            epic.setStatus(Status.DONE);
        } else if (allIsInNewCount == list.size()) {
            epic.setStatus(Status.NEW);
        } else {
            epic.setStatus(Status.IN_PROGRESS);
        }
    }

    void validateTask(Task task) {
        if (task.getName() == null || task.getName().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be null or empty.");
        }
        if (task.getDescription() == null) {
            throw new IllegalArgumentException("Task description cannot be null.");
        }
    }
}