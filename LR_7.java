import java.util.Scanner;

class WeakPasswordException extends IllegalArgumentException {
    public WeakPasswordException(String message) {
        super(message);
    }
}

//Класс для валидации пароля
class PasswordValidator {
    private static final int MIN_LENGTH = 8;

    public static void validatePassword(String password) {
        if (password == null || password.length() < MIN_LENGTH) {
            throw new WeakPasswordException(
                    "Пароль слишком короткий! Минимум " + MIN_LENGTH + " символов"
            );
        }

        if (!password.matches(".*[0-9].*")) {
            throw new WeakPasswordException(
                    "Пароль должен содержать хотя бы одну цифру"
            );
        }
    }
}

public class LR_7 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите пароль для проверки: ");
        String password = scanner.nextLine();

        try {
            PasswordValidator.validatePassword(password);
            System.out.println("Пароль надежный!");
        } catch (WeakPasswordException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }

        scanner.close();
    }
}