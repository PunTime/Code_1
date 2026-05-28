/* Варіант 12
 * 3 процеси, 2 ресурси:
 * P1 -> ресурс 1
 * P2 -> ресурс 2
 * P3 -> ресурс 1,2
 */

class SharedBuffer {

    /* Спільний буфер */
    private int buffer = -1;

    /* Кількість зайнятих буферів:
     * 0 — буфер порожній
     * 1 — буфер зайнятий
     */
    private int occupiedBuffers = 0;

    /*
     * Метод запису в буфер
     */
    public synchronized void set(int value) {

        String name = Thread.currentThread().getName();

        /*
         * Якщо буфер зайнятий —
         * Producer переходить в очікування
         */
        while (occupiedBuffers == 1) {

            try {
                System.out.println(name + " намагається записати дані");
                System.out.println("Буфер заповнений. "
                        + name + " очікує.\n");

                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * Запис значення
         */
        buffer = value;

        /*
         * Позначаємо буфер як зайнятий
         */
        occupiedBuffers = 1;

        displayState(name + " записав: " + buffer);

        /*
         * Повідомляємо Consumer
         */
        notifyAll();
    }

    /*
     * Метод читання з буфера
     */
    public synchronized int get() {

        String name = Thread.currentThread().getName();

        /*
         * Якщо буфер порожній —
         * Consumer переходить в очікування
         */
        while (occupiedBuffers == 0) {

            try {
                System.out.println(name + " намагається читати дані");
                System.out.println("Буфер порожній. "
                        + name + " очікує.\n");

                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * Читання значення
         */
        int value = buffer;

        /*
         * Позначаємо буфер як порожній
         */
        occupiedBuffers = 0;

        displayState(name + " прочитав: " + value);

        /*
         * Повідомляємо Producer
         */
        notifyAll();

        return value;
    }

    /*
     * Вивід стану буфера
     */
    public void displayState(String operation) {

        System.out.println(operation);
        System.out.println("buffer = " + buffer);
        System.out.println("occupiedBuffers = "
                + occupiedBuffers);
        System.out.println();
    }
}

/*
 * Потік Producer
 */
class Producer implements Runnable {

    private SharedBuffer sharedLocation;

    public Producer(SharedBuffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run() {

        /*
         * Producer записує числа від 1 до 4
         */
        for (int count = 1; count <= 4; count++) {

            sharedLocation.set(count);

            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Producer завершив роботу\n");
    }
}

/*
 * Потік Consumer
 */
class Consumer implements Runnable {

    private SharedBuffer sharedLocation;

    public Consumer(SharedBuffer sharedLocation) {
        this.sharedLocation = sharedLocation;
    }

    @Override
    public void run() {

        int sum = 0;

        /*
         * Consumer читає 4 значення
         */
        for (int count = 1; count <= 4; count++) {

            int value = sharedLocation.get();

            sum += value;

            try {
                Thread.sleep(500);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * Правильна сума:
         * 1 + 2 + 3 + 4 = 10
         */
        System.out.println("Сума прочитаних значень: "
                + sum);

        System.out.println("Consumer завершив роботу");
    }
}

/*
 * Головний клас
 */
public class Main {

    public static void main(String[] args) {

        /*
         * Створення спільного буфера
         */
        SharedBuffer sharedLocation =
                new SharedBuffer();

        /*
         * Створення потоків
         */
        Thread producer =
                new Thread(
                        new Producer(sharedLocation),
                        "Producer"
                );

        Thread consumer =
                new Thread(
                        new Consumer(sharedLocation),
                        "Consumer"
                );

        /*
         * Запуск потоків
         */
        producer.start();
        consumer.start();
    }
}