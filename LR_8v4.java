import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

public class LR_8v4 {

    // Имена файлов со ссылками
    private static final String MUSIC_TXT = "inFile.txt";
    private static final String PICS_TXT = "imgFile.txt";

    // Папки, куда будем сохранять
    private static final String DIR_MUSIC = "music/";
    private static final String DIR_PICS = "images/";

    public static void main(String[] args) {
        try {
            // Создаем папки, если их нет
            Files.createDirectories(Paths.get(DIR_MUSIC));
            Files.createDirectories(Paths.get(DIR_PICS));

            // Первый поток занимается музыкой
            Thread t1 = new Thread(() -> processMusic(
                    MUSIC_TXT, DIR_MUSIC, "track", ".mp3"
            ));

            // Второй поток качает картинки
            Thread t2 = new Thread(() -> processImages(
                    PICS_TXT, DIR_PICS, "image"
            ));

            // Запускаем оба потока
            t1.start();
            t2.start();

            // Ждем, пока оба закончат работу
            t1.join();
            t2.join();

            System.out.println("\nВсе загрузки успешно завершены!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для обработки списка музыки (тут расширение файла известно заранее)
    private static void processMusic(String fileInput, String dirOut, String nameStart, String ext) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String link;
            int k = 0; // счетчик

            // Читаем файл построчно
            while ((link = br.readLine()) != null) {
                // Собираем путь: папка + имя + номер + расширение
                String path = dirOut + nameStart + k + ext;
                System.out.println("[Музыка] Качаю " + link);

                try {
                    download(link, path);
                    System.out.println("[Музыка] Готово: " + path);
                } catch (IOException ex) {
                    System.err.println("[Музыка] Ошибка: " + link + " — " + ex.getMessage());
                }

                k++;
            }

        } catch (IOException e) {
            System.err.println("Не смог открыть файл " + fileInput);
        }
    }

    // Метод для картинок (тут расширение узнаем из ссылки)
    private static void processImages(String fileInput, String dirOut, String nameStart) {
        try (BufferedReader br = new BufferedReader(new FileReader(fileInput))) {
            String link;
            int k = 0;

            while ((link = br.readLine()) != null) {
                // Определяем тип файла (.jpg, .png...)
                String ext = findExt(link);
                String path = dirOut + nameStart + k + ext;

                System.out.println("[Картинки] Качаю " + link);

                try {
                    download(link, path);
                    System.out.println("[Картинки] Готово: " + path);
                } catch (IOException ex) {
                    System.err.println("⚠[Картинки] Ошибка: " + link + " — " + ex.getMessage());
                }

                k++;
            }

        } catch (IOException e) {
            System.err.println("⚠Не смог открыть файл " + fileInput);
        }
    }

    // Вспомогательный метод: ищем точку в конце ссылки
    private static String findExt(String url) {
        int dotIndex = url.lastIndexOf('.');
        // Проверяем, что точка есть и она не в начале пути
        if (dotIndex != -1 && dotIndex > url.lastIndexOf('/')) {
            return url.substring(dotIndex);
        }
        // Если не нашли, ставим заглушку
        return ".img";
    }

    // Непосредственно скачивание через NIO
    private static void download(String link, String targetFile) throws IOException {
        URL url = new URL(link);
        java.net.URLConnection conn = url.openConnection();

        // Притворяемся браузером
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36");

        // Переливаем байты из интернета в файл
        try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
             FileOutputStream fos = new FileOutputStream(targetFile)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}