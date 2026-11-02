package edu.course.lab02;
public class ProjectTask {
private final String title;
private final String description;
private TaskStatus status;
private int estimatedHours;

public ProjectTask(String title, String description, int estimatedHours) {
    if (title == null || title.isBlank()) {
        throw new IllegalArgumentException("Title cannot be empty");
    }

    if (description == null || description.isBlank()) {
        throw new IllegalArgumentException("Description cannot be empty");
    }

    if (estimatedHours <= 0) {
        throw new IllegalArgumentException("Estimated hours must be positive");
    }

    this.title = title;
    this.description = description;
    this.estimatedHours = estimatedHours;
    this.status = TaskStatus.TODO;
}

public String getTitle() {
    return title;
}

public String getDescription() {
    return description;
}

public TaskStatus getStatus() {
    return status;
}

public int getEstimatedHours() {
    return estimatedHours;
}

public void start() {
    if (status != TaskStatus.TODO) {
        throw new IllegalStateException("Task cannot be started");
    }

    status = TaskStatus.IN_PROGRESS;
}

public void complete() {
    if (status != TaskStatus.IN_PROGRESS) {
        throw new IllegalStateException("Task cannot be completed");
    }

    status = TaskStatus.DONE;
}

public boolean isCompleted() {
    return status == TaskStatus.DONE;
}

public void addEstimatedHours(int hours) {
    if (hours <= 0) {
        throw new IllegalArgumentException("Hours must be positive");
    }

    estimatedHours += hours;
}}