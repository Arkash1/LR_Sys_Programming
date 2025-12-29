import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

public class LR_8v3 {

    // Откуда читаем ссылки
    private static final String INPUT_FILE = "inFile.txt";
    // Куда сохраняем картинки
    private static final String DIR = "images/";

    public static void main(String[] args) {
        try {
            // Создаем папку, если её нет
            Files.createDirectories(Paths.get(DIR));

            // Читаем файл со ссылками
            try (BufferedReader br = new BufferedReader(new FileReader(INPUT_FILE))) {
                String link;
                int k = 0; // счетчик файлов

                // Проходим по каждой строке
                while ((link = br.readLine()) != null) {
                    // Определяем расширение (.jpg, .png и т.д.)
                    String ext = getExt(link);
                    // Собираем полное имя файла
                    String path = DIR + "image" + k + ext;

                    System.out.println("Качаю картинку: " + link);

                    try {
                        // Загружаем
                        download(link, path);
                        System.out.println("Сохранил в: " + path);
                        k++;
                    } catch (IOException ex) {
                        System.err.println("Не вышло скачать: " + link + " — " + ex.getMessage());
                    }
                }
            }

            System.out.println("Готово! Все картинки на месте.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод, чтобы узнать формат файла из ссылки
    private static String getExt(String text) {
        int dotIndex = text.lastIndexOf('.');
        // Если точка есть и она не в начале пути
        if (dotIndex != -1 && dotIndex > text.lastIndexOf('/')) {
            return text.substring(dotIndex);
        }
        // Если не поняли что это, пусть будет .img
        return ".img";
    }

    // Метод для скачивания
    private static void download(String urlStr, String filePath) throws IOException {
        URL url = new URL(urlStr);
        java.net.URLConnection conn = url.openConnection();

        // Притворяемся браузером (Chrome), чтобы пустили
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36");

        // Открываем потоки и переливаем данные
        try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
             FileOutputStream fos = new FileOutputStream(filePath)) {
            // Скачиваем всё сразу
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}