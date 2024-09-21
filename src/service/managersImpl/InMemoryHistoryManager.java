package service.managersImpl;

import model.Task;
import service.interfaces.HistoryManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryHistoryManager implements HistoryManager {

    @Override
    public void add(Task task) {
        Task taskNew = new Task(task.getId(), task.getName(), task.getDescription(), task.getStatus());
        removeNode(nodeMap.get(taskNew.getId()));
        linkLast(taskNew);
        nodeMap.put(taskNew.getId(), last);
    }

    @Override
    public List<Task> getHistory() {
        return getTasks();
    }

    @Override
    public void remove(int id) {
        Node node = nodeMap.remove(id);
        removeNode(node);
    }

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task, Node prev, Node next) {
            this.task = task;
            this.prev = prev;
            this.next = next;
        }
    }

    private final Map<Integer, Node> nodeMap = new HashMap<>();
    private Node first;
    private Node last;

    private ArrayList<Task> getTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        Node node = first;
        if (node != null) {
            while (node.next != null) {
                tasks.add(node.task);
                node = node.next;
            }
            tasks.add(node.task);
        }

        return tasks;
    }

    private void linkLast(Task task) {
        final Node node = new Node(task, last, null);
        if (last == null) {
            first = node;
        } else {
            last.next = node;
        }
        last = node;
    }

    private void removeNode(Node node) {
        if (node == null) return;

        if (node == first && node == last) {
            first = null;
            last = null;
            return;
        }

        if (node == first) {
            first = node.next;
            if (first != null) {
                first.prev = null;
            }
        } else if (node == last) {
            last = node.prev;
            if (last != null) {
                last.next = null;
            }
        } else {
            node.next.prev = node.prev;
            node.prev.next = node.next;
        }
    }
}
