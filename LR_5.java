import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.*;

public class LR_5 {

    // Вспомогательный метод просто для копирования одного файла
    public static void copySimple(File from, File to) throws IOException {
        Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    // Метод запускает копирование по очереди (сначала один, потом второй)
    public static void oneByOne(File in1, File out1, File in2, File out2) throws IOException {
        copySimple(in1, out1);
        copySimple(in2, out2);
    }

    // Метод запускает копирование параллельно (сразу оба)
    public static void withThreads(File in1, File out1, File in2, File out2)
            throws InterruptedException, ExecutionException {

        // Делаем пул на 2 потока
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // Задача 1
        Callable<Void> job1 = () -> {
            copySimple(in1, out1);
            return null;
        };

        // Задача 2
        Callable<Void> job2 = () -> {
            copySimple(in2, out2);
            return null;
        };

        // Отправляем задачи на выполнение
        Future<Void> res1 = pool.submit(job1);
        Future<Void> res2 = pool.submit(job2);

        // Ждем, пока обе закончатся
        res1.get();
        res2.get();

        // Закрываем пул
        pool.shutdown();
    }

    public static void main(String[] args) {
        // Файлы для обычного теста
        File f1 = new File("input1.txt");
        File o1 = new File("output1.txt");
        File f2 = new File("input2.txt");
        File o2 = new File("output2.txt");

        try {
            // Засекаем время
            long timeStart = System.nanoTime();
            oneByOne(f1, o1, f2, o2);
            long timeEnd = System.nanoTime();

            System.out.println("Обычное копирование заняло (мс): " + ((timeEnd - timeStart) / 1_000_000));

            // Файлы для параллельного теста
            File pf1 = new File("input1.txt");
            File po1 = new File("output1_par.txt");
            File pf2 = new File("input2.txt");
            File po2 = new File("output2_par.txt");

            // Засекаем время снова
            timeStart = System.nanoTime();
            withThreads(pf1, po1, pf2, po2);
            timeEnd = System.nanoTime();

            System.out.println("Параллельное копирование заняло (мс): " + ((timeEnd - timeStart) / 1_000_000));

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}