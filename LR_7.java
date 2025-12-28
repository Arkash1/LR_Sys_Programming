import java.util.Scanner;

public class Main {

    // Мой класс ошибки, если бананов не хватит
    public static class LowBananasException extends Exception {
        public LowBananasException(String text) {
            super(text);
        }
    }

    // Класс для проверки условий
    public static class Checker {
        // Константа: минимум нужно 3 штуки
        private static final int MIN_COUNT = 3;

        // Метод проверяет количество
        public static void validate(int n) throws LowBananasException {
            if (n < MIN_COUNT) {
                // Если мало, кидаем ошибку
                throw new LowBananasException(
                        "Ты принёс всего " + n + " банан(а/ов)! А нужно минимум " + MIN_COUNT + "!\n" +
                                "Обезьянка расстроена("
                );
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Сколько бананов ты взял для обезьянки? ");
        // Считываем число введенное пользователем
        int count = sc.nextInt();

        try {
            // Пробуем проверить число через наш чекер
            Checker.validate(count);
            System.out.println("Обезьянка в восторге! ");
        } catch (LowBananasException ex) {
            // Если поймали нашу ошибку, выводим сообщение
            System.out.println("Ошибка:");
            System.out.println(ex.getMessage());
        }

        sc.close();
    }
}
