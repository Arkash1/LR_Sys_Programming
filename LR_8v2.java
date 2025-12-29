import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;

public class LR_8v2 {

    // Файл со ссылками
    private static final String SOURCE_FILE = "inFile.txt";
    // Папка, куда всё складываем
    private static final String OUT_FOLDER = "music/";

    public static void main(String[] args) {
        try {
            // Создаем папку, если ее нет
            Files.createDirectories(Paths.get(OUT_FOLDER));

            // Читаем файл
            try (BufferedReader reader = new BufferedReader(new FileReader(SOURCE_FILE))) {
                String link;
                int n = 0; // Номер трека

                // Бежим по строчкам
                while ((link = reader.readLine()) != null) {
                    // Придумываем имя файла
                    String path = OUT_FOLDER + "track" + n + ".mp3";
                    System.out.println("Качаю: " + link);

                    try {
                        // Вызываем наш метод скачивания
                        download(link, path);
                        System.out.println("Готово: " + path);
                        n++;
                    } catch (IOException ex) {
                        System.err.println("⚠ Ошибка загрузки: " + link + " — " + ex.getMessage());
                    }
                }
            }

            System.out.println("Скачивание закончено!");

            // Теперь ищем что-нибудь, чтобы послушать
            Path dir = Paths.get(OUT_FOLDER);
            try {
                // Ищем первый попавшийся mp3 файл в папке
                Path song = Files.list(dir)
                        .filter(p -> p.toString().toLowerCase().endsWith(".mp3"))
                        .findFirst()
                        .orElse(null);

                if (song != null) {
                    System.out.println("Запускаю плеер для файла: " + song);
                    play(song.toFile());
                } else {
                    System.out.println("⚠ Музыки в папке не нашлось.");
                }
            } catch (IOException ex) {
                System.err.println("Ошибка поиска файлов: " + ex.getMessage());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Метод для скачивания файла
    private static void download(String urlStr, String filePath) throws IOException {
        URL url = new URL(urlStr);
        java.net.URLConnection conn = url.openConnection();

        // Притворяемся браузером, чтобы сервер не ругался
        conn.setRequestProperty("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                        + "(KHTML, like Gecko) Chrome/124.0 Safari/537.36");

        // Переливаем данные из сети в файл
        try (ReadableByteChannel rbc = Channels.newChannel(conn.getInputStream());
             FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        }
    }

    // Метод для открытия файла в системе
    private static void play(File file) {
        try {
            // Просим винду (или другую ОС) открыть файл стандартной программой
            java.awt.Desktop.getDesktop().open(file);
        } catch (IOException e) {
            System.err.println("⚠ Не получилось открыть плеер: " + e.getMessage());
        }
    }
}