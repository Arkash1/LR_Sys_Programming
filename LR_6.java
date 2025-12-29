import java.nio.file.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class LR_6 {

    // Обычный метод копирования
    public static void doCopy(String src, String dst) throws Exception {
        Path p1 = Paths.get(src);
        Path p2 = Paths.get(dst);

        // Создаем папку, если её нет
        Files.createDirectories(p2.getParent());

        // Копируем с заменой, если файл уже есть
        Files.copy(p1, p2, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Файл перелетел: " + src + " -> " + dst);
    }

    // Асинхронный метод (возвращает задачу)
    public static CompletableFuture<Void> doCopyAsync(String src, String dst) {
        return CompletableFuture.runAsync(() -> {
            try {
                // Внутри вызываем обычное копирование
                doCopy(src, dst);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }, Executors.newSingleThreadExecutor()); // Запускаем в отдельном потоке
    }

    public static void main(String[] args) throws Exception {
        // Создадим файл, чтобы было что копировать
        Files.writeString(Paths.get("data.txt"), "Тут какой-то текст для проверки");

        System.out.println("--- ПО ОЧЕРЕДИ ---");
        long t1 = System.currentTimeMillis();

        // Копируем просто так
        doCopy("data.txt", "result/copy_simple.txt");

        long delta = System.currentTimeMillis() - t1;
        System.out.println("Справились за: " + delta + " мс\n");

        System.out.println("--- ПАРАЛЛЕЛЬНО ---");
        t1 = System.currentTimeMillis();

        // Запускаем сразу три задачи
        CompletableFuture<Void> task1 = doCopyAsync("data.txt", "result/fast1.txt");
        CompletableFuture<Void> task2 = doCopyAsync("data.txt", "result/fast2.txt");
        CompletableFuture<Void> task3 = doCopyAsync("data.txt", "result/fast3.txt");

        // Ждем, пока выполнятся все три
        CompletableFuture.allOf(task1, task2, task3).join();

        delta = System.currentTimeMillis() - t1;
        System.out.println("Все копии готовы за: " + delta + " мс");
    }
}