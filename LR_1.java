import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class LR_1 {
    private static List<Process> runningProcesses = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== Менеджер процессов ===");

        while (true) {
            printMenu();
            int choice = getIntInput("Выберите действие: ");

            switch (choice) {
                case 1:
                    startProcess();
                    break;
                case 2:
                    listProcesses();
                    break;
                case 3:
                    terminateProcess();
                    break;
                case 4:
                    System.out.println("Выход из программы.");
                    return;
                default:
                    System.out.println("Неверный выбор. Попробуйте снова.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n1. Запустить процесс");
        System.out.println("2. Показать запущенные процессы");
        System.out.println("3. Завершить процесс");
        System.out.println("4. Выход");
    }

    private static void startProcess() {
        try {
            System.out.print("Введите имя процесса для запуска: ");
            String processName = scanner.nextLine().trim();

            if (processName.isEmpty()) {
                System.out.println("Имя процесса не может быть пустым.");
                return;
            }

            Process process;

            // Определяем команду в зависимости от операционной системы
            String os = System.getProperty("os.name").toLowerCase();
            String command;

            if (os.contains("win")) {
                // Windows
                if (processName.equalsIgnoreCase("notepad")) {
                    command = "notepad.exe";
                } else if (processName.equalsIgnoreCase("calc")) {
                    command = "calc.exe";
                } else if (processName.equalsIgnoreCase("mspaint")) {
                    command = "mspaint.exe";
                } else {
                    command = processName;
                }
            } else {
                // Linux/Mac
                command = processName;
            }

            process = Runtime.getRuntime().exec(command);
            runningProcesses.add(process);

            System.out.println("Процесс '" + processName + "' запущен. PID: " + getProcessId(process));
            printProcessInfo(process);

        } catch (IOException e) {
            System.out.println("Ошибка при запуске процесса: " + e.getMessage());
        }
    }

    private static void listProcesses() {
        if (runningProcesses.isEmpty()) {
            System.out.println("Нет запущенных процессов.");
            return;
        }

        System.out.println("\n=== Запущенные процессы ===");
        for (int i = 0; i < runningProcesses.size(); i++) {
            Process process = runningProcesses.get(i);
            System.out.println((i + 1) + ". PID: " + getProcessId(process) +
                    ", Alive: " + process.isAlive());
            printProcessInfo(process);
        }
    }

    private static void terminateProcess() {
        if (runningProcesses.isEmpty()) {
            System.out.println("Нет запущенных процессов для завершения.");
            return;
        }

        listProcesses();
        int processIndex = getIntInput("Выберите номер процесса для завершения: ") - 1;

        if (processIndex < 0 || processIndex >= runningProcesses.size()) {
            System.out.println("Неверный номер процесса.");
            return;
        }

        Process process = runningProcesses.get(processIndex);

        if (!process.isAlive()) {
            System.out.println("Процесс уже завершен.");
            runningProcesses.remove(processIndex);
            return;
        }

        System.out.print("Завершить процесс? (д/н): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("д") || confirmation.equals("y") || confirmation.equals("да")) {
            try {
                process.destroy();
                if (process.isAlive()) {
                    process.destroyForcibly();
                }
                runningProcesses.remove(processIndex);
                System.out.println("Процесс завершен.");
            } catch (Exception e) {
                System.out.println("Ошибка при завершении процесса: " + e.getMessage());
            }
        } else {
            System.out.println("Завершение процесса отменено.");
        }
    }

    private static long getProcessId(Process process) {
        try {
            // Для Java 9+ можно использовать process.pid()
            if (process.getClass().getName().equals("java.lang.ProcessImpl")) {
                java.lang.reflect.Field pidField = process.getClass().getDeclaredField("pid");
                pidField.setAccessible(true);
                return pidField.getLong(process);
            }
        } catch (Exception e) {
            // Если не удалось получить PID через reflection
        }

        // Альтернативный способ получения информации о процессе
        String processInfo = process.toString();
        try {
            // Извлекаем PID из строкового представления
            if (processInfo.contains("pid=")) {
                String pidStr = processInfo.substring(processInfo.indexOf("pid=") + 4);
                pidStr = pidStr.split(",")[0].split(" ")[0].replace("}", "");
                return Long.parseLong(pidStr);
            }
        } catch (Exception e) {
            // Если не удалось извлечь PID
        }

        return -1; // Неизвестный PID
    }

    private static void printProcessInfo(Process process) {
        try {
            long pid = getProcessId(process);
            System.out.println("   PID: " + (pid != -1 ? pid : "неизвестен"));
            System.out.println("   Статус: " + (process.isAlive() ? "запущен" : "завершен"));
            System.out.println("   Поток ошибок: " + (process.getErrorStream().available() > 0 ? "есть данные" : "пуст"));
            System.out.println("   Выходной поток: " + (process.getInputStream().available() > 0 ? "есть данные" : "пуст"));
        } catch (IOException e) {
            System.out.println("   Ошибка при получении информации: " + e.getMessage());
        }
    }

    private static int getIntInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Пожалуйста, введите целое число.");
            }
        }
    }
}