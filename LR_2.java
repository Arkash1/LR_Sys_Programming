// Класс для работы в отдельном потоке
class Worker implements Runnable {
    Thread t;
    private String workerName;

    // Конструктор
    public Worker(String workerName) {
        this.workerName = workerName;
        // Создаем сам поток
        t = new Thread(this, workerName);
        System.out.println("Инфо о потоке: " + t);
        t.start(); // Запускаем
    }

    @Override
    public void run() {
        System.out.println(workerName + " начал работу!");

        // Цикл с обратным отсчетом
        for (int k = 5; k > 0; k--) {
            System.out.println(workerName + " ждет: " + k);
            try {
                // Спим 1 секунду
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.out.println(workerName + " кто-то разбудил раньше времени");
            }
        }
        System.out.println(workerName + " закончил дела.");
    }
}

public class LR_2 {
    public static void main(String[] args) {
        System.out.println(" Старт Main ");

        // Создаем 10 штук
        for (int i = 1; i <= 10; i++) {
            new Worker("Работяга #" + i);
        }

        System.out.println(" Конец Main ");
    }
}