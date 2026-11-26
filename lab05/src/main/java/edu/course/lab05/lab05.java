package edu.course.lab05;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

enum TaskStatus {
    NEW,
    READY,
    IN_PROGRESS,
    DONE
}

final class Task {
    private final String id;
    private final String title;
    private final String category;
    private final int estimatedHours;
    private final TaskStatus status;

    Task(String id, String title, String category, int estimatedHours, TaskStatus status) {
        this.id = Objects.requireNonNull(id, "id");
        this.title = Objects.requireNonNull(title, "title");
        this.category = Objects.requireNonNull(category, "category");
        this.estimatedHours = estimatedHours;
        this.status = Objects.requireNonNull(status, "status");
    }

    String getId() { return id; }
    String getTitle() { return title; }
    String getCategory() { return category; }
    int getEstimatedHours() { return estimatedHours; }
    TaskStatus getStatus() { return status; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task)) return false;
        Task other = (Task) o;
        return estimatedHours == other.estimatedHours
                && id.equals(other.id)
                && title.equals(other.title)
                && category.equals(other.category)
                && status == other.status;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, title, category, estimatedHours, status);
    }

    @Override
    public String toString() {
        return "Task{id=" + id + ", title=" + title + ", category=" + category
                + ", estimatedHours=" + estimatedHours + ", status=" + status + "}";
    }
}

final class ValidationError {
    private final int lineNumber;
    private final String field;
    private final String message;

    ValidationError(int lineNumber, String field, String message) {
        this.lineNumber = lineNumber;
        this.field = field;
        this.message = message;
    }

    int getLineNumber() { return lineNumber; }
    String getField() { return field; }
    String getMessage() { return message; }

    @Override
    public String toString() {
        return "line " + lineNumber + ", field '" + field + "': " + message;
    }
}

final class LoadResult {
    private final List<Task> tasks;
    private final List<ValidationError> errors;

    LoadResult(List<Task> tasks, List<ValidationError> errors) {
        this.tasks = Collections.unmodifiableList(new ArrayList<>(tasks));
        this.errors = Collections.unmodifiableList(new ArrayList<>(errors));
    }

    List<Task> getTasks() { return tasks; }
    List<ValidationError> getErrors() { return errors; }

    boolean hasErrors() { return !errors.isEmpty(); }
}

final class DataLoadingException extends RuntimeException {
    DataLoadingException(String message, Throwable cause) {
        super(message, cause);
    }

    DataLoadingException(String message) {
        super(message);
    }
}

final class TaskFileLoader {
    private static final String SEPARATOR = ";";
    private static final String EXPECTED_HEADER = "id;title;category;estimatedHours;status";
    private static final int FIELD_COUNT = 5;

    LoadResult load(Path path) {
        List<String> lines;
        try {
            lines = Files.readAllLines(path, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new DataLoadingException("Cannot read " + path, e);
        }

        if (lines.isEmpty()) {
            throw new DataLoadingException("File is empty, header is missing: " + path);
        }
        String header = stripBom(lines.get(0)).trim();
        if (!EXPECTED_HEADER.equals(header)) {
            throw new DataLoadingException(
                    "Unexpected header in " + path + ": '" + header + "', expected '" + EXPECTED_HEADER + "'");
        }

        List<Task> tasks = new ArrayList<>();
        List<ValidationError> errors = new ArrayList<>();

        for (int i = 1; i < lines.size(); i++) {
            String line = lines.get(i);
            int lineNumber = i + 1;

            if (line.isBlank()) {
                continue;
            }

            String[] fields = line.split(SEPARATOR, -1);
            if (fields.length != FIELD_COUNT) {
                errors.add(new ValidationError(lineNumber, "line",
                        "Expected " + FIELD_COUNT + " fields but found " + fields.length));
                continue;
            }

            int errorsBefore = errors.size();
            Task task = parseRow(fields, lineNumber, errors);
            if (errors.size() == errorsBefore) {
                tasks.add(task);
            }
        }
        return new LoadResult(tasks, errors);
    }

    private Task parseRow(String[] fields, int lineNumber, List<ValidationError> errors) {
        String id = fields[0].trim();
        String title = fields[1].trim();
        String category = fields[2].trim();
        String hoursText = fields[3].trim();
        String statusText = fields[4].trim();

        requireNotEmpty(id, "id", lineNumber, errors);
        requireNotEmpty(title, "title", lineNumber, errors);
        requireNotEmpty(category, "category", lineNumber, errors);

        int hours = 0;
        if (hoursText.isEmpty()) {
            errors.add(new ValidationError(lineNumber, "estimatedHours", "Field must not be empty"));
        } else {
            try {
                hours = Integer.parseInt(hoursText);
                if (hours <= 0) {
                    errors.add(new ValidationError(lineNumber, "estimatedHours",
                            "Hours must be positive: " + hours));
                }
            } catch (NumberFormatException e) {
                errors.add(new ValidationError(lineNumber, "estimatedHours",
                        "Not a number: '" + hoursText + "'"));
            }
        }

        TaskStatus status = null;
        if (statusText.isEmpty()) {
            errors.add(new ValidationError(lineNumber, "status", "Field must not be empty"));
        } else {
            try {
                status = TaskStatus.valueOf(statusText);
            } catch (IllegalArgumentException e) {
                errors.add(new ValidationError(lineNumber, "status", "Unknown status: '" + statusText + "'"));
            }
        }

        if (id.isEmpty() || title.isEmpty() || category.isEmpty() || status == null || hours <= 0) {
            return null;
        }
        return new Task(id, title, category, hours, status);
    }

    private void requireNotEmpty(String value, String field, int lineNumber, List<ValidationError> errors) {
        if (value.isEmpty()) {
            errors.add(new ValidationError(lineNumber, field, "Field must not be empty"));
        }
    }

    private String stripBom(String s) {
        return !s.isEmpty() && s.charAt(0) == '\uFEFF' ? s.substring(1) : s;
    }
}