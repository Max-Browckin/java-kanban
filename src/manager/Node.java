package manager;

import model.Task;

public class Node {
        private Task task;
        private manager.Node prev;
        private manager.Node next;

        public manager.Node getNext() {
            return next;
        }

        public manager.Node getPrev() {
            return prev;
        }

        public Task getTask() {
            return task;
        }

        public void setNext(manager.Node next) {
            this.next = next;
        }

        public void setPrev(manager.Node prev) {
            this.prev = prev;
        }

        public void setTask(Task task) {
            this.task = task;
        }
    }

