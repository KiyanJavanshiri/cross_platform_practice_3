
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Scanner;

public class NumberMenuApp {
    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;

    private static final int DEFAULT_CAPACITY = 10;
    private static final int MIN_CAPACITY = 1;
    private static final int MAX_CAPACITY = 50;

    public static void main(String[] args) {
        int capacity = DEFAULT_CAPACITY;
        int startIndex = 0;

        // Перевірка необов'язкового параметра місткості.
        if (args.length > 0 && args[0].startsWith("--capacity=")) {
            String capacityText = args[0].substring("--capacity=".length());

            if (capacityText.strip().isEmpty()) {
                System.err.println("Помилка: місткість не задано.");
                return;
            }

            try {
                capacity = Integer.parseInt(capacityText.strip());
            } catch (NumberFormatException exception) {
                System.err.printf(
                        "Помилка: \"%s\" не є цілою місткістю.%n",
                        capacityText);
                return;
            }

            if (capacity < MIN_CAPACITY || capacity > MAX_CAPACITY) {
                System.err.println(
                        "Помилка: місткість має бути від 1 до 50.");
                return;
            }

            startIndex = 1;
        }

        int[] values = new int[capacity];

        int count = loadArguments(args, startIndex, values);

        if (count < 0) {
            return;
        }

        int successfulChanges = 0;

        System.out.println("Початкових значень: " + count);

        try (Scanner scanner = new Scanner(
                System.in, StandardCharsets.UTF_8)) {

            boolean running = true;

            while (running) {
                printMenu();

                Integer choice = readIntInRange(
                        scanner, "Оберіть пункт: ", 0, 8);

                if (choice == null) {
                    System.out.println("\nВвід завершено.");
                    break;
                }

                switch (choice) {
                    case 1 -> showValues(values, count);

                    case 2 -> {
                        int oldCount = count;
                        count = addValue(scanner, values, count);

                        if (count > oldCount) {
                            successfulChanges++;
                        }
                    }

                    case 3 -> printStatistics(values, count);

                    case 4 -> countOccurrences(scanner, values, count);

                    case 5 -> {
                        if (replaceValue(scanner, values, count)) {
                            successfulChanges++;
                        }
                    }

                    case 6 -> {
                        int oldCount = count;
                        count = deleteValue(scanner, values, count);

                        if (count < oldCount) {
                            successfulChanges++;
                        }
                    }

                    case 7 -> showRange(scanner, values, count);

                    case 8 -> {
                        sortAscending(values, count);
                        System.out.println("Масив відсортовано.");
                    }

                    case 0 -> running = false;

                    default -> throw new IllegalStateException(
                            "Непередбачений пункт меню: " + choice);
                }
            }
        }

        System.out.println("Успішних змін: " + successfulChanges);
        System.out.println("Роботу програми завершено.");
    }

    static int loadArguments(
            String[] args, int startIndex, int[] values) {

        int initialValuesCount = args.length - startIndex;

        if (initialValuesCount > values.length) {
            System.err.printf(
                    "Помилка: кількість початкових значень "
                            + "перевищує місткість %d.%n",
                    values.length);
            return -1;
        }

        for (int index = 0; index < initialValuesCount; index++) {
            String text = args[startIndex + index].strip();

            if (text.isEmpty()) {
                System.err.printf(
                        "Помилка аргументу %d: порожнє значення.%n",
                        index + 1);
                return -1;
            }

            try {
                int value = Integer.parseInt(text);

                if (value < MIN_VALUE || value > MAX_VALUE) {
                    System.err.printf(
                            "Помилка аргументу %d: значення має бути "
                                    + "від %d до %d.%n",
                            index + 1, MIN_VALUE, MAX_VALUE);
                    return -1;
                }

                values[index] = value;

            } catch (NumberFormatException exception) {
                System.err.printf(
                        "Помилка аргументу %d: \"%s\" "
                                + "не є цілим числом.%n",
                        index + 1, args[startIndex + index]);
                return -1;
            }
        }

        return initialValuesCount;
    }

    static void printMenu() {
        System.out.println();
        System.out.println("1 — Показати значення");
        System.out.println("2 — Додати значення");
        System.out.println("3 — Показати статистику");
        System.out.println("4 — Порахувати входження");
        System.out.println("5 — Змінити значення");
        System.out.println("6 — Видалити значення");
        System.out.println("7 — Показати діапазон");
        System.out.println("8 — Сортувати за зростанням");
        System.out.println("0 — Завершити роботу");
    }

    static Integer readIntInRange(
            Scanner scanner, String prompt, int min, int max) {

        while (true) {
            System.out.print(prompt);

            if (!scanner.hasNextLine()) {
                return null;
            }

            String line = scanner.nextLine().strip();

            if (line.isEmpty()) {
                System.out.println("Помилка: введіть ціле число.");
                continue;
            }

            try {
                int value = Integer.parseInt(line);

                if (value < min || value > max) {
                    System.out.printf(
                            "Помилка: значення має бути від %d до %d.%n",
                            min, max);
                    continue;
                }

                return value;

            } catch (NumberFormatException exception) {
                System.out.println("Помилка: введіть ціле число.");
            }
        }
    }

    static void showValues(int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }

        System.out.print("Значення: [");

        for (int index = 0; index < count; index++) {
            if (index > 0) {
                System.out.print(", ");
            }

            System.out.print(values[index]);
        }

        System.out.println("]");
    }

    static int addValue(
            Scanner scanner, int[] values, int count) {

        if (count == values.length) {
            System.out.println(
                    "Масив заповнено. Додавання неможливе.");
            return count;
        }

        Integer value = readIntInRange(
                scanner,
                "Введіть значення від 0 до 100: ",
                MIN_VALUE,
                MAX_VALUE);

        if (value == null) {
            System.out.println(
                    "Додавання скасовано: ввід завершено.");
            return count;
        }

        values[count] = value;
        count++;

        System.out.println(
                "Значення додано. Кількість елементів: " + count);

        return count;
    }

    static void printStatistics(int[] values, int count) {
        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }

        long sum = 0;
        int minimum = values[0];
        int maximum = values[0];

        int evenCount = 0;
        int oddCount = 0;

        for (int index = 0; index < count; index++) {
            int value = values[index];

            sum += value;

            if (value < minimum) {
                minimum = value;
            }

            if (value > maximum) {
                maximum = value;
            }

            if (value % 2 == 0) {
                evenCount++;
            } else {
                oddCount++;
            }
        }

        double average = (double) sum / count;

        int notLessThanAverageCount = 0;

        for (int index = 0; index < count; index++) {
            if (values[index] >= average) {
                notLessThanAverageCount++;
            }
        }

        System.out.printf(
                Locale.ROOT,
                "Кількість: %d%nСума: %d%nМінімум: %d%n"
                        + "Максимум: %d%nСереднє: %.2f%n",
                count, sum, minimum, maximum, average);

        System.out.println("Парних: " + evenCount);
        System.out.println("Непарних: " + oddCount);
        System.out.println(
                "Не менших за середнє: "
                        + notLessThanAverageCount);
    }

    static void countOccurrences(
            Scanner scanner, int[] values, int count) {

        if (count == 0) {
            System.out.println("Дані відсутні.");
            return;
        }

        Integer target = readIntInRange(
                scanner,
                "Значення для пошуку: ",
                MIN_VALUE,
                MAX_VALUE);

        if (target == null) {
            System.out.println(
                    "Пошук скасовано: ввід завершено.");
            return;
        }

        int occurrences = 0;

        for (int index = 0; index < count; index++) {
            if (values[index] == target) {
                occurrences++;
            }
        }

        System.out.printf(
                "Значення %d трапляється %d раз(и).%n",
                target, occurrences);
    }

    static boolean replaceValue(
            Scanner scanner, int[] values, int count) {

        if (count == 0) {
            System.out.println("Дані відсутні.");
            return false;
        }

        Integer position = readIntInRange(
                scanner,
                "Введіть номер елемента: ",
                1,
                count);

        if (position == null) {
            System.out.println(
                    "Заміна скасована: ввід завершено.");
            return false;
        }

        Integer newValue = readIntInRange(
                scanner,
                "Введіть нове значення від 0 до 100: ",
                MIN_VALUE,
                MAX_VALUE);

        if (newValue == null) {
            System.out.println(
                    "Заміна скасована: ввід завершено.");
            return false;
        }

        int index = position - 1;
        int oldValue = values[index];

        values[index] = newValue;

        System.out.printf(
                "Елемент %d змінено: %d -> %d.%n",
                position, oldValue, newValue);

        return true;
    }

    static int deleteValue(
            Scanner scanner, int[] values, int count) {

        if (count == 0) {
            System.out.println("Дані відсутні.");
            return count;
        }

        Integer position = readIntInRange(
                scanner,
                "Введіть номер елемента для видалення: ",
                1,
                count);

        if (position == null) {
            System.out.println(
                    "Видалення скасовано: ввід завершено.");
            return count;
        }

        int index = position - 1;

        for (int current = index; current < count - 1; current++) {
            values[current] = values[current + 1];
        }

        count--;

        values[count] = 0;

        System.out.printf(
                "Елемент %d видалено. Кількість елементів: %d.%n",
                position, count);

        return count;
    }

    static void showRange(
            Scanner scanner, int[] values, int count) {

        Integer lower = readIntInRange(
                scanner,
                "Введіть нижню межу від 0 до 100: ",
                MIN_VALUE,
                MAX_VALUE);

        if (lower == null) {
            System.out.println(
                    "Вибірку скасовано: ввід завершено.");
            return;
        }

        Integer upper = readIntInRange(
                scanner,
                "Введіть верхню межу від 0 до 100: ",
                MIN_VALUE,
                MAX_VALUE);

        if (upper == null) {
            System.out.println(
                    "Вибірку скасовано: ввід завершено.");
            return;
        }

        if (lower > upper) {
            System.out.println(
                    "Помилка: нижня межа перевищує верхню.");
            return;
        }

        StringBuilder result = new StringBuilder();
        int matches = 0;

        for (int index = 0; index < count; index++) {
            if (values[index] >= lower && values[index] <= upper) {
                if (matches > 0) {
                    result.append(", ");
                }

                result.append(values[index]);
                matches++;
            }
        }

        if (matches == 0) {
            System.out.printf(
                    "У діапазоні [%d, %d] значень немає.%n",
                    lower, upper);
        } else {
            System.out.printf(
                    "У діапазоні [%d, %d]: [%s]%n",
                    lower, upper, result);
        }
    }

    static void sortAscending(int[] values, int count) {
        for (int i = 0; i < count - 1; i++) {
            for (int j = i + 1; j < count; j++) {
                if (values[i] > values[j]) {
                    int temporary = values[i];
                    values[i] = values[j];
                    values[j] = temporary;
                }
            }
        }
    }
}

