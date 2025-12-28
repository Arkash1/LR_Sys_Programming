import java.io.IOException;
import java.util.Scanner;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Создаем сканер для чтения с клавиатуры
        Scanner sc = new Scanner(System.in);

        try {
            System.out.println("Выберите процесс для запуска:");
            System.out.println("1 - Блокнот");
            System.out.println("2 - Калькулятор");
            System.out.println("3 - Пэйнт");
            System.out.println("4 - Все процессы сразу");
            System.out.print("Ваш выбор: ");
            
            // Читаем выбранный номер
            int num = sc.nextInt();
            sc.nextLine(); // пропускаем перенос строки

            // Сюда будем складывать названия программ
            List<String> progNames = new ArrayList<>();

            // Смотрим, что выбрал пользователь
            switch (num) {
                case 1 -> progNames.add("notepad.exe");
                case 2 -> progNames.add("calc.exe");
                case 3 -> progNames.add("mspaint.exe");
                case 4 -> {
                    // Добавляем сразу всё
                    progNames.add("notepad.exe");
                    progNames.add("calc.exe");
                    progNames.add("mspaint.exe");
                }
                default -> {
                    System.out.println("Ошибка, выберите другой процесс");
                    return; // выходим
                }
            }

            // Список самих запущенных процессов
            List<Process> activeProcs = new ArrayList<>();
            // Мапа чтобы помнить какой процесс как называется
            Map<String, Process> procsMap = new HashMap<>();

            // Пробегаем по названиям и запускаем
            for (String name : progNames) {
                try {
                    Process p = new ProcessBuilder(name).start();
                    activeProcs.add(p);
                    procsMap.put(name, p);
                    System.out.println("Процесс " + name + " запущен с PID: " + p.pid());
                } catch (IOException ex) {
                    System.out.println("Ошибка запуска " + name + ": " + ex.getMessage());
                }
            }

            // Если список пустой, значит ничего не вышло
            if (activeProcs.isEmpty()) {
                System.out.println("Ни один процесс не был запущен!");
                return;
            }

            System.out.print("\nЗакрыть все процессы? (д/н): ");
            String res = sc.nextLine();

            // Если ответили "д"
            if (res.equalsIgnoreCase("д")) {
                // Закрываем всё
                for (Process p : activeProcs) {
                    p.destroy();
                }
                System.out.println("Все процессы завершены.");

                System.out.println("\nИнформация о всех процессах:");

                // Выводим инфу по каждому
                for (Map.Entry<String, Process> entry : procsMap.entrySet()) {
                    String name = entry.getKey();
                    Process p = entry.getValue();

                    // Собираем данные
                    Map<String, String> info = new HashMap<>();
                    info.put("Имя процесса", name);
                    info.put("Пиды", String.valueOf(p.pid()));

                    try {
                        info.put("Код завершения", String.valueOf(p.exitValue()));
                    } catch (IllegalThreadStateException ex) {
                        info.put("Код завершения", "Ещё не завершился");
                    }

                    info.put("Статус выполнения", String.valueOf(p.isAlive()));

                    System.out.println("\n--- " + name + " ---");
                    for (Map.Entry<String, String> item : info.entrySet()) {
                        System.out.println(item.getKey() + ": " + item.getValue());
                    }
                    System.out.println("----------------------");
                }

                System.out.println("\nСписок всех PID'ов:");
                for (Process p : activeProcs) {
                    System.out.println("PID: " + p.pid());
                }

            } else {
                // Если решили оставить
                System.out.println("Процессы оставлены работать.");

                System.out.println("\nСписок всех запущенных PID'ов:");
                for (Process p : activeProcs) {
                    // Тут используем вспомогательный метод для имени
                    System.out.println("PID: " + p.pid() + " (" +
                            findName(procsMap, p) + ")");
                }
            }

        } catch (Exception e) {
            System.out.println("Ошибка: " + e.getMessage());
        } finally {
            sc.close();
        }
    }

    // Метод ищет название проги по самому процессу
    private static String findName(Map<String, Process> map, Process p) {
        for (Map.Entry<String, Process> entry : map.entrySet()) {
            if (entry.getValue().equals(p)) {
                return entry.getKey();
            }
        }
        return "Неизвестный процесс";
    }
}
