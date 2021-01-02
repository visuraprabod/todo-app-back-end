package lk.ijse.dep.web.model;

import lk.ijse.dep.web.util.Priority;

public class Task {
    private String id;
    private String text;
    private boolean completed;
    private int priority;

    public Task(String id, String text, boolean completed, int priority) {
        this.setId(id);
        this.setText(text);
        this.setCompleted(completed);
        this.setPriority(priority);
    }

    public Task() {
    }

    @Override
    public String toString() {
        return "TaskTest{" +
                "id='" + id + '\'' +
                ", text='" + text + '\'' +
                ", completed=" + completed +
                ", priority='" + priority + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
