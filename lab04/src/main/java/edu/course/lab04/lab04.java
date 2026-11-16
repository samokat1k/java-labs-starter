package edu.course.lab04;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

final class Book {
    private final String isbn;
    private final String title;

    Book(String isbn, String title) {
        this.isbn = Objects.requireNonNull(isbn, "isbn");
        this.title = Objects.requireNonNull(title, "title");
    }

    String getIsbn() { return isbn; }
    String getTitle() { return title; }

    // равенство определяется только isbn
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return isbn.equals(((Book) o).isbn);
    }

    @Override
    public int hashCode() { return isbn.hashCode(); }

    @Override
    public String toString() { return "Book{isbn='" + isbn + "', title='" + title + "'}"; }
}

final class IssueId {
    private final String projectCode;
    private final int number;

    IssueId(String projectCode, int number) {
        Objects.requireNonNull(projectCode, "projectCode");
        if (projectCode.isBlank()) {
            throw new IllegalArgumentException("projectCode must not be blank");
        }
        if (number <= 0) {
            throw new IllegalArgumentException("number must be positive: " + number);
        }
        this.projectCode = projectCode;
        this.number = number;
    }

    String getProjectCode() { return projectCode; }
    int getNumber() { return number; }

    // равенство определяется полями projectCode и number
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IssueId other = (IssueId) o;
        return number == other.number && projectCode.equals(other.projectCode);
    }

    @Override
    public int hashCode() { return Objects.hash(projectCode, number); }

    @Override
    public String toString() { return projectCode + "-" + number; }
}

final class ProjectTask {
    private final String title;
    private final String assignee;

    ProjectTask(String title, String assignee) {
        this.title = Objects.requireNonNull(title, "title");
        this.assignee = Objects.requireNonNull(assignee, "assignee");
    }

    String getTitle() { return title; }
    String getAssignee() { return assignee; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProjectTask other = (ProjectTask) o;
        return title.equals(other.title) && assignee.equals(other.assignee);
    }

    @Override
    public int hashCode() { return Objects.hash(title, assignee); }

    @Override
    public String toString() {
        return "ProjectTask{title='" + title + "', assignee='" + assignee + "'}";
    }
}

class IssueRegistry {
    private final Map<IssueId, ProjectTask> tasks = new HashMap<>();

    ProjectTask put(IssueId id, ProjectTask task) {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(task, "task");
        return tasks.put(id, task);
    }

    ProjectTask get(IssueId id) { return tasks.get(id); }

    ProjectTask remove(IssueId id) { return tasks.remove(id); }

    boolean containsKey(IssueId id) { return tasks.containsKey(id); }

    int size() { return tasks.size(); }
}