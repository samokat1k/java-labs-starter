package edu.course.lab03;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Lab03Test {

    // Тесты скидок

    @Test
    void noDiscountReturnsSamePrice() {
        assertEquals(100, new NoDiscount().apply(100));
    }

    @Test
    void noDiscountRejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new NoDiscount().apply(-10));
    }

    @Test
    void percentDiscountCalculatesCorrectly() {
        assertEquals(80, new PercentDiscount(20).apply(100));
    }

    @Test
    void percentDiscountRejectsInvalidPercent() {
        assertThrows(IllegalArgumentException.class,
                () -> new PercentDiscount(120));
    }

    @Test
    void percentDiscountRejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class,
                () -> new PercentDiscount(20).apply(-10));
    }

    @Test
    void priceCalculatorRejectsNullPolicy() {
        assertThrows(NullPointerException.class,
                () -> new PriceCalculator(null));
    }

    // Тесты ProjectTask

    @Test
    void projectTaskStoresValues() {
        ProjectTask task = new ProjectTask("Test", 5, TaskStatus.TODO);

        assertEquals("Test", task.getTitle());
        assertEquals(5, task.getEffortHours());
        assertEquals(TaskStatus.TODO, task.getStatus());
    }

    @Test
    void projectTaskRejectsNegativeEffort() {
        assertThrows(IllegalArgumentException.class,
                () -> new ProjectTask("Test", -1, TaskStatus.TODO));
    }

    @Test
    void projectTaskRejectsNullTitle() {
        assertThrows(NullPointerException.class,
                () -> new ProjectTask(null, 5, TaskStatus.TODO));
    }

    @Test
    void projectTaskRejectsNullStatus() {
        assertThrows(NullPointerException.class,
                () -> new ProjectTask("Test", 5, null));
    }

    // Тесты приоритетов

    @Test
    void effortPrioritySelectsTaskWithLessHours() {
        ProjectTask quick = new ProjectTask("Quick", 2, TaskStatus.TODO);
        ProjectTask big = new ProjectTask("Big", 20, TaskStatus.DONE);

        TaskPrioritizer prioritizer = new TaskPrioritizer(new EffortPriorityPolicy());

        assertSame(quick, prioritizer.pickHigher(quick, big));
    }

    @Test
    void statusPrioritySelectsTaskInProgress() {
        ProjectTask todo = new ProjectTask("Todo", 2, TaskStatus.TODO);
        ProjectTask progress = new ProjectTask("Progress", 20, TaskStatus.IN_PROGRESS);

        TaskPrioritizer prioritizer = new TaskPrioritizer(new StatusPriorityPolicy());

        assertSame(progress, prioritizer.pickHigher(todo, progress));
    }

    @Test
    void prioritizerRejectsNullPolicy() {
        assertThrows(NullPointerException.class,
                () -> new TaskPrioritizer(null));
    }

    @Test
    void prioritizerRejectsNullTasks() {
        ProjectTask task = new ProjectTask("Test", 5, TaskStatus.TODO);

        TaskPrioritizer prioritizer =
                new TaskPrioritizer(new EffortPriorityPolicy());

        assertThrows(NullPointerException.class,
                () -> prioritizer.pickHigher(null, task));

        assertThrows(NullPointerException.class,
                () -> prioritizer.pickHigher(task, null));
    }
}
