package edu.course.lab02;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
class ProjectTaskTest {
@Test
void shouldCreateTask() {
    ProjectTask task = new ProjectTask("Website", "Create a website", 5);

    assertEquals("Website", task.getTitle());
    assertEquals("Create a website", task.getDescription());
    assertEquals(5, task.getEstimatedHours());
    assertEquals(TaskStatus.TODO, task.getStatus());
}

@Test
void shouldThrowExceptionForEmptyTitle() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new ProjectTask("", "Description", 5)
    );
}

@Test
void shouldThrowExceptionForInvalidHours() {
    assertThrows(
            IllegalArgumentException.class,
            () -> new ProjectTask("Website", "Description", 0)
    );
}

@Test
void shouldStartTask() {
    ProjectTask task = new ProjectTask("Website", "Description", 5);

    task.start();

    assertEquals(TaskStatus.IN_PROGRESS, task.getStatus());
}

@Test
void shouldCompleteTask() {
    ProjectTask task = new ProjectTask("Website", "Description", 5);

    task.start();
    task.complete();

    assertTrue(task.isCompleted());
    assertEquals(TaskStatus.DONE, task.getStatus());
}

@Test
void shouldAddEstimatedHours() {
    ProjectTask task = new ProjectTask("Website", "Description", 5);

    task.addEstimatedHours(3);

    assertEquals(8, task.getEstimatedHours());
}

@Test
void shouldThrowExceptionWhenStartingTaskTwice() {
    ProjectTask task = new ProjectTask("Website", "Description", 5);

    task.start();

    assertThrows(IllegalStateException.class, task::start);
}}