package edu.course.lab01;

/**
 * Небольшие методы для первой лабораторной работы.
 */
public final class CourseToolkit {

    private CourseToolkit() {
        // Утилитарный класс не должен иметь экземпляров.
    }

    /**
     * Возвращает true, если число четное.
     */
    public static boolean isEven(int number) {
        return number % 2 == 0;
    }

    /**
     *возвращает true если число простое
     */
    public static boolean isPrime(int number) {
        if (number < 2) {
            return false;
        }

        for (int i = 2; i <= number / i; i++) {
            if (number % i == 0) {
                return false;
            }
        }

        return true;
    }
    /**
     *возвращает true, если строка является палиндромом
     */
    public static boolean isPalindrome(String text) {
        if (text == null) {
            throw new IllegalArgumentException();
        }

        String reversed = new StringBuilder(text).reverse().toString();
        return text.equals(reversed);
    }
    /**
     *возвращает среднее арифметическое элементов массива
     */
    public static double average(int[] values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException();
        }

        long sum = 0;

        for (int value : values) {
            sum += value;
        }

        return (double) sum / values.length;
    }
}