// Класс для бегуна (потока)
class Runner extends Thread {
    private String name;
    private int dist = 0; // текущая дистанция
    private static int FINAL = 111; // сколько бежать

    public Runner(String name, int prio) {
        this.name = name;
        this.setName(name);
        // Устанавливаем важность потока
        this.setPriority(prio);
    }

    // Чтобы узнать, сколько пробежал
    public int getDist() {
        return dist;
    }

    @Override
    public void run() {
        // Бежим пока не дойдем до финиша
        while (dist < FINAL) {
            dist++;
            System.out.println(name + " на отметке: " + dist + " м");
            try {
                // Небольшая задержка
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println(name + " всё, добежал!");
    }
}

public class LR_3 {
    public static void main(String[] args) {
        // Создаем зайца (приоритет повыше)
        Runner r = new Runner("Заяц", Thread.NORM_PRIORITY + 2);
        // Создаем черепаху (приоритет пониже)
        Runner t = new Runner("Черепаха", Thread.NORM_PRIORITY - 2);

        r.start();
        t.start();

        // Пока оба бегут, следим за ними
        while (r.isAlive() && t.isAlive()) {
            // Если заяц сильно ушел вперед (> 5 метров)
            if (r.getDist() - t.getDist() > 5) {
                // Даем ускорение черепахе, а зайца тормозим
                t.setPriority(Thread.MAX_PRIORITY);
                r.setPriority(Thread.MIN_PRIORITY);
            }
            // Если черепаха обогнала зайца
            else if (t.getDist() - r.getDist() > 5) {
                // Ускоряем зайца
                r.setPriority(Thread.MAX_PRIORITY);
                t.setPriority(Thread.MIN_PRIORITY);
            }
            else {
                // Если бегут рядом, ставим обычный приоритет
                r.setPriority(Thread.NORM_PRIORITY);
                t.setPriority(Thread.NORM_PRIORITY);
            }
        }

        System.out.println("Гонка завершена.");
    }
}