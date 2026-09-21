package edu.course.lab01;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CourseToolkitTest {

    @Test
    void returnsTrueForEvenNumber() {
        boolean result = CourseToolkit.isEven(8);

        assertTrue(result);
    }

    @Test
    void returnsFalseForOddNumber() {
        boolean result = CourseToolkit.isEven(7);

        assertFalse(result);
    }

    @Test
    void returnsTrueForZero() {
        boolean result = CourseToolkit.isEven(0);

        assertTrue(result);
    }

    @Test
    void returnsTrueForPrimeNumber() {
        assertTrue(CourseToolkit.isPrime(7));
    }

    @Test
    void returnsTrueForTwo() {
        assertTrue(CourseToolkit.isPrime(2));
    }

    @Test
    void returnsFalseForCompositeNumber() {
        assertFalse(CourseToolkit.isPrime(12));
    }

    @Test
    void returnsFalseForNumberLessThanTwo() {
        assertFalse(CourseToolkit.isPrime(1));
    }
    @Test
    void returnsTrueForPalindrome() {
        assertTrue(CourseToolkit.isPalindrome("level"));
    }

    @Test
    void returnsFalseWhenCaseDiffers() {
        assertFalse(CourseToolkit.isPalindrome("Level"));
    }

    @Test
    void returnsFalseWhenSpacesDiffer() {
        assertFalse(CourseToolkit.isPalindrome("a b c"));
    }
    @Test
    void returnsAverageForNormalValues() {
        assertEquals(2.0, CourseToolkit.average(new int[]{1, 2, 3}));
    }

    @Test
    void returnsAverageForNegativeValues() {
        assertEquals(-2.0, CourseToolkit.average(new int[]{-1, -2, -3}));
    }

    @Test
    void throwsExceptionForEmptyArray() {
        assertThrows(
                IllegalArgumentException.class,
                () -> CourseToolkit.average(new int[]{})
        );
    }
}