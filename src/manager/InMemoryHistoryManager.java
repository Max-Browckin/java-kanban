package manager;

import model.Task;

import java.util.*;

public class InMemoryHistoryManager implements HistoryManager {
    private static class CustomLinkedList {
        private final Map<Integer, Node> table = new HashMap<>();
        private Node head;
        private Node tail;

        private static class Node {
            private Task task;
            private Node prev;
            private Node next;

            public Task getTask() {
                return task;
            }

            public void setTask(Task task) {
                this.task = task;
            }

            public Node getPrev() {
                return prev;
            }

            public void setPrev(Node prev) {
                this.prev = prev;
            }

            public Node getNext() {
                return next;
            }

            public void setNext(Node next) {
                this.next = next;
            }
        }

        private void linkLast(Task task) {
            Node element = new Node();
            element.setTask(task);

            if (table.containsKey(task.getId())) {
                removeNode(table.get(task.getId()));
            }

            if (head == null) {
                tail = element;
                head = element;
                element.setNext(null);
                element.setPrev(null);
            } else {
                element.setPrev(tail);
                element.setNext(null);
                tail.setNext(element);
                tail = element;
            }

            table.put(task.getId(), element);
        }

        private List<Task> getTasks() {
            List<Task> result = new ArrayList<>();
            Node element = head;
            while (element != null) {
                result.add(element.getTask());
                element = element.getNext();
            }
            return result;
        }

        private void removeNode(Node node) {
            if (node != null) {
                table.remove(node.getTask().getId());
                Node prev = node.getPrev();
                Node next = node.getNext();

                if (head == node) {
                    head = node.getNext();
                }
                if (tail == node) {
                    tail = node.getPrev();
                }

                if (prev != null) {
                    prev.setNext(next);
                }

                if (next != null) {
                    next.setPrev(prev);
                }
            }
        }

        private Node getNode(int id) {
            return table.get(id);
        }
    }

    private final CustomLinkedList list = new CustomLinkedList();


    @Override
    public void add(Task task) {
        list.linkLast(task);
    }


    @Override
    public void remove(int id) {
        list.removeNode(list.getNode(id));
    }


    @Override
    public List<Task> getHistory() {
        return list.getTasks();
    }
}


