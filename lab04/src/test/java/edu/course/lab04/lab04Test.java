package edu.course.lab04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void valuePutByOneInstanceIsFoundByAnotherInstanceWithSameIsbn() {
        Book stored = new Book("978-5-00-000000-1", "Effective Java");
        Book probe = new Book("978-5-00-000000-1", "Другое название");

        Map<Book, String> shelf = new HashMap<>();
        shelf.put(stored, "A-12");

        assertTrue(shelf.containsKey(probe));
        assertEquals("A-12", shelf.get(probe));
        assertEquals(stored.hashCode(), probe.hashCode());
    }

    @Test
    void booksWithDifferentIsbnAreNotEqual() {
        Book first = new Book("978-5-00-000000-1", "Effective Java");
        Book second = new Book("978-5-00-000000-2", "Effective Java");

        assertNotEquals(first, second);

        Map<Book, String> shelf = new HashMap<>();
        shelf.put(first, "A-12");
        assertFalse(shelf.containsKey(second));
    }
}

class IssueRegistryTest {

    // ---------- обычные тесты операций ----------

    @Test
    void putAndGetReturnStoredValue() {
        IssueRegistry registry = new IssueRegistry();
        ProjectTask task = new ProjectTask("Fix login", "alice");

        assertNull(registry.put(new IssueId("WEB", 1), task));

        assertEquals(task, registry.get(new IssueId("WEB", 1)));
        assertEquals(1, registry.size());
    }

    @Test
    void getMissingKeyReturnsNull() {
        assertNull(new IssueRegistry().get(new IssueId("WEB", 1)));
    }

    @Test
    void removeDeletesValueAndReturnsIt() {
        IssueRegistry registry = new IssueRegistry();
        ProjectTask task = new ProjectTask("Fix login", "alice");
        registry.put(new IssueId("WEB", 1), task);

        assertEquals(task, registry.remove(new IssueId("WEB", 1)));

        assertFalse(registry.containsKey(new IssueId("WEB", 1)));
        assertEquals(0, registry.size());
        assertNull(registry.remove(new IssueId("WEB", 1)));
    }

    @Test
    void containsKeyAndSizeWork() {
        IssueRegistry registry = new IssueRegistry();
        assertEquals(0, registry.size());
        assertFalse(registry.containsKey(new IssueId("WEB", 1)));

        registry.put(new IssueId("WEB", 1), new ProjectTask("A", "alice"));
        registry.put(new IssueId("WEB", 2), new ProjectTask("B", "bob"));

        assertTrue(registry.containsKey(new IssueId("WEB", 2)));
        assertEquals(2, registry.size());
    }

    @Test
    void issueIdRejectsInvalidArguments() {
        assertThrows(NullPointerException.class, () -> new IssueId(null, 1));
        assertThrows(IllegalArgumentException.class, () -> new IssueId(" ", 1));
        assertThrows(IllegalArgumentException.class, () -> new IssueId("WEB", 0));
        assertThrows(IllegalArgumentException.class, () -> new IssueId("WEB", -5));
    }

    // ---------- обязательные эксперименты ----------

    // 1. Два разных экземпляра равного ключа находят одно значение
    @Test
    void twoDistinctInstancesOfEqualKeyFindSameValue() {
        IssueId first = new IssueId("WEB", 7);
        IssueId second = new IssueId("WEB", 7);
        assertNotSame(first, second);
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());

        IssueRegistry registry = new IssueRegistry();
        ProjectTask task = new ProjectTask("Fix login", "alice");
        registry.put(first, task);

        assertEquals(task, registry.get(second));
        assertTrue(registry.containsKey(second));
    }

    // 2. Повторное добавление с равным ключом заменяет значение, размер не растёт
    @Test
    void putWithEqualKeyReplacesValueAndKeepsSize() {
        IssueRegistry registry = new IssueRegistry();
        ProjectTask oldTask = new ProjectTask("Old", "alice");
        ProjectTask newTask = new ProjectTask("New", "bob");

        registry.put(new IssueId("WEB", 7), oldTask);
        ProjectTask previous = registry.put(new IssueId("WEB", 7), newTask);

        assertEquals(oldTask, previous);
        assertEquals(1, registry.size());
        assertEquals(newTask, registry.get(new IssueId("WEB", 7)));
    }

    // 3. Неравные ключи с одинаковым hashCode сохраняют разные значения
    // ("Aa" и "BB" имеют одинаковый String.hashCode())
    @Test
    void collidingButUnequalKeysKeepDifferentValues() {
        IssueId a = new IssueId("Aa", 1);
        IssueId b = new IssueId("BB", 1);

        assertEquals(a.hashCode(), b.hashCode(), "hashCode должен совпадать (коллизия)");
        assertNotEquals(a, b, "коллизия не означает логического равенства");

        IssueRegistry registry = new IssueRegistry();
        ProjectTask taskA = new ProjectTask("Task A", "alice");
        ProjectTask taskB = new ProjectTask("Task B", "bob");
        registry.put(a, taskA);
        registry.put(b, taskB);

        assertEquals(2, registry.size());
        assertEquals(taskA, registry.get(new IssueId("Aa", 1)));
        assertEquals(taskB, registry.get(new IssueId("BB", 1)));
    }

    // 4. Сравнение с null и объектом другого типа даёт false
    @Test
    void equalsReturnsFalseForNullAndOtherType() {
        IssueId id = new IssueId("WEB", 1);

        assertFalse(id.equals(null));
        assertFalse(id.equals("WEB-1"));
        assertFalse(id.equals(Integer.valueOf(1)));
    }

    // 5. Изменяемый учебный ключ: после изменения поля поиск перестаёт работать
    @Test
    void mutableKeyIsLostAfterFieldChange() {
        MutableKey key = new MutableKey("before");
        Map<MutableKey, String> map = new HashMap<>();
        map.put(key, "value");
        assertEquals("value", map.get(key));

        key.name = "after";

        assertNull(map.get(key), "поиск по изменённому ключу не находит значение");
        assertFalse(map.containsKey(key));
        assertNull(map.get(new MutableKey("before")), "поиск по старому значению тоже не находит");
        assertEquals(1, map.size(), "запись при этом всё ещё лежит в карте");
    }

    /** Учебный изменяемый ключ — только для эксперимента. */
    private static final class MutableKey {
        private String name;

        private MutableKey(String name) { this.name = name; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            return Objects.equals(name, ((MutableKey) o).name);
        }

        @Override
        public int hashCode() { return Objects.hashCode(name); }
    }
}