// Класс для потока "Яйцо"
class Egg extends Thread {
    @Override
    public void run() {
        for (int k = 0; k < 5; k++) {
            System.out.println("Яйцо!");
            try {
                // Небольшая пауза
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

// Класс для потока "Курица"
class Hen extends Thread {
    @Override
    public void run() {
        for (int k = 0; k < 5; k++) {
            System.out.println("Курица!");
            try {
                // Тоже спим
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }
}

public class LR_4 {
    public static void main(String[] args) {
        // Создаем участников спора
        Egg t1 = new Egg();
        Hen t2 = new Hen();

        // Начинаем
        t1.start();
        t2.start();

        try {
            // Главный поток ждет, пока закончит поток "Яйцо"
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Проверяем, работает ли еще курица
        if (t2.isAlive()) {
            try {
                // Если работает, ждем её завершения
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Если яйцо закончилось, а курица еще была жива -> курица победила
            System.out.println("Вывод: Сначала была курица!");
        } else {
            // Если курица уже всё, а мы ждали яйцо -> яйцо победило
            System.out.println("Вывод: Сначала было яйцо!");
        }
    }
}