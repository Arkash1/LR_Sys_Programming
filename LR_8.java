import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

public class LR_8 {

    // Имя файла, откуда берем ссылки
    private static final String INPUT = "inFile.txt";
    // Папка для сохранения музыки
    private static final String DIR = "music/";

    public static void main(String[] args) {
        try {
            // Создаем директорию, если её нет
            Files.createDirectories(Paths.get(DIR));

            // Открываем файл для чтения
            try (BufferedReader reader = new BufferedReader(new FileReader(INPUT))) {
                String link;
                int i = 0; // счетчик треков

                // Читаем ссылки пока файл не кончится
                while ((link = reader.readLine()) != null) {
                    // Формируем имя файла (song0.mp3, song1.mp3 и т.д.)
                    String name = DIR + "song" + i + ".mp3";
                    System.out.println("Начинаю качать: " + link);

                    try {
                        // Пробуем скачать
                        saveFile(link, name);
                        System.out.println("Успешно: " + name);
                        i++;
                    } catch (IOException ex) {
                        System.err.println("⚠ Сбой загрузки: " + link + " — " + ex.getMessage());
                    }
                }
            }

            System.out.println("Весь список обработан!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод скачивания (используем NIO)
    private static void saveFile(String urlStr, String path) throws IOException {
        URL url = new URL(urlStr);
        java.net.URLConnection conn = url.openConnection();

        // Ставим User-Agent, чтобы притвориться браузером (иначе могут не дать скачать)
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36");

        // Создаем каналы для чтения и записи
        try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
             FileOutputStream fos = new FileOutputStream(path)) {

            // Переливаем данные из интернета в файл
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }
}