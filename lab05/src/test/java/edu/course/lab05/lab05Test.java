package edu.course.lab05;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class lab05Test {
    private static final String HEADER = "id;title;category;estimatedHours;status";

    @TempDir
    Path tempDir;

    private final TaskFileLoader loader = new TaskFileLoader();

    // ===== Подготовительная часть: numbers.txt (10, bad, 20) =====

    private Path createNumbersFile() throws IOException {
        Path file = tempDir.resolve("numbers.txt");
        Files.write(file, List.of("10", "bad", "20"), StandardCharsets.UTF_8);
        return file;
    }

    @Test
    void fileContainsThreeExpectedLines() throws IOException {
        List<String> lines = Files.readAllLines(createNumbersFile(), StandardCharsets.UTF_8);
        assertEquals(List.of("10", "bad", "20"), lines);
    }

    @Test
    void validLinesAreConvertedToIntegersInLoop() throws IOException {
        List<String> lines = Files.readAllLines(createNumbersFile(), StandardCharsets.UTF_8);

        List<Integer> numbers = new ArrayList<>();
        for (String line : lines) {
            try {
                numbers.add(Integer.parseInt(line));
            } catch (NumberFormatException e) {
                // "bad" пропускаем
            }
        }
        assertEquals(List.of(10, 20), numbers);
    }

    @Test
    void badLineThrowsNumberFormatException() {
        assertThrows(NumberFormatException.class, () -> Integer.parseInt("bad"));
    }

    // ===== Тесты TaskFileLoader =====

    private Path write(String... rows) throws IOException {
        Path file = tempDir.resolve("tasks.csv");
        List<String> lines = new ArrayList<>();
        lines.add(HEADER);
        lines.addAll(List.of(rows));
        Files.write(file, lines, StandardCharsets.UTF_8);
        return file;
    }

    @Test
    void validFile() throws IOException {
        LoadResult result = loader.load(write("T-101;Prepare release;backend;8;READY"));

        assertFalse(result.hasErrors());
        assertEquals(1, result.getTasks().size());
        Task t = result.getTasks().get(0);
        assertEquals("T-101", t.getId());
        assertEquals("Prepare release", t.getTitle());
        assertEquals("backend", t.getCategory());
        assertEquals(8, t.getEstimatedHours());
        assertEquals(TaskStatus.READY, t.getStatus());
    }

    @Test
    void cyrillicTextSurvivesUtf8RoundTrip() throws IOException {
        LoadResult result = loader.load(write("T-1;Подготовить релиз;бэкенд;5;NEW"));

        assertTrue(result.getErrors().isEmpty());
        assertEquals("Подготовить релиз", result.getTasks().get(0).getTitle());
        assertEquals("бэкенд", result.getTasks().get(0).getCategory());
    }

    @Test
    void emptyFieldIsReported() throws IOException {
        LoadResult result = loader.load(write("T-1;;backend;8;READY"));

        assertTrue(result.getTasks().isEmpty());
        assertEquals(1, result.getErrors().size());
        ValidationError e = result.getErrors().get(0);
        assertEquals(2, e.getLineNumber());
        assertEquals("title", e.getField());
    }

    @Test
    void invalidNumberIsReported() throws IOException {
        LoadResult result = loader.load(write("T-1;Title;backend;abc;READY"));

        assertTrue(result.getTasks().isEmpty());
        assertEquals("estimatedHours", result.getErrors().get(0).getField());
    }

    @Test
    void nonPositiveHoursIsReported() throws IOException {
        LoadResult result = loader.load(write("T-1;Title;backend;0;READY"));

        assertTrue(result.getTasks().isEmpty());
        assertEquals("estimatedHours", result.getErrors().get(0).getField());
    }

    @Test
    void unknownStatusIsReported() throws IOException {
        LoadResult result = loader.load(write("T-1;Title;backend;8;UNKNOWN"));

        assertTrue(result.getTasks().isEmpty());
        assertEquals("status", result.getErrors().get(0).getField());
    }

    @Test
    void wrongColumnCountIsReported() throws IOException {
        LoadResult result = loader.load(write("T-1;Title;backend;8"));

        assertTrue(result.getTasks().isEmpty());
        assertEquals(1, result.getErrors().size());
        assertEquals(2, result.getErrors().get(0).getLineNumber());
    }

    @Test
    void validAndInvalidRowsAreProcessedTogether() throws IOException {
        LoadResult result = loader.load(write(
                "T-1;First;backend;3;READY",
                "T-2;Second;backend;oops;READY",
                "",
                "T-3;Third;frontend;5;DONE",
                "T-4;Fourth;frontend;2;WRONG"));

        assertEquals(2, result.getTasks().size());
        assertEquals("T-1", result.getTasks().get(0).getId());
        assertEquals("T-3", result.getTasks().get(1).getId());

        assertEquals(2, result.getErrors().size());
        assertEquals(3, result.getErrors().get(0).getLineNumber());
        assertEquals(6, result.getErrors().get(1).getLineNumber());
    }

    @Test
    void missingFileThrowsDataLoadingExceptionWithCause() {
        Path missing = tempDir.resolve("no-such-file.csv");

        DataLoadingException ex = assertThrows(DataLoadingException.class, () -> loader.load(missing));

        assertNotNull(ex.getCause());
        assertTrue(ex.getCause() instanceof NoSuchFileException);
    }

    @Test
    void wrongHeaderIsSourceError() throws IOException {
        Path file = tempDir.resolve("bad-header.csv");
        Files.write(file, List.of("a;b;c", "T-1;Title;backend;8;READY"), StandardCharsets.UTF_8);

        assertThrows(DataLoadingException.class, () -> loader.load(file));
    }
}