/*
 * Варіант 12
 *
 * Кількість процесів: 3
 * Кількість ресурсів: 2
 *
 * Процес 1 -> ресурс 1
 * Процес 2 -> ресурс 2
 * Процес 3 -> ресурс 1, 2
 */

class Resource {

    /*
     * Номер ресурсу
     */
    private final int id;

    /*
     * Стан ресурсу:
     * true  - зайнятий
     * false - вільний
     */
    private boolean busy = false;

    public Resource(int id) {
        this.id = id;
    }

    /*
     * Метод захоплення ресурсу
     */
    public synchronized void acquire(String processName) {

        /*
         * Якщо ресурс зайнятий —
         * процес переходить в очікування
         */
        while (busy) {

            try {

                System.out.println(
                        "  [" + processName
                        + "] очiкує Ресурс "
                        + id + "..."
                );

                wait();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /*
         * Позначаємо ресурс як зайнятий
         */
        busy = true;

        System.out.println(
                "  [" + processName
                + "] >>> захопив Ресурс "
                + id
        );
    }

    /*
     * Метод звільнення ресурсу
     */
    public synchronized void release(String processName) {

        /*
         * Позначаємо ресурс як вільний
         */
        busy = false;

        System.out.println(
                "  [" + processName
                + "] <<< звiльнив Ресурс "
                + id
        );

        /*
         * Повідомляємо інші потоки
         */
        notifyAll();
    }
}

/*
 * Клас процесу
 */
class ProcessThread extends Thread {

    /*
     * Ресурси процесу
     */
    private final Resource[] resources;

    /*
     * Кількість повторень
     */
    private final int iterations;

    public ProcessThread(
            String name,
            int iterations,
            Resource... resources
    ) {

        super(name);

        this.iterations = iterations;
        this.resources = resources;
    }

    @Override
    public void run() {

        /*
         * Виконання ітерацій
         */
        for (int i = 1; i <= iterations; i++) {

            System.out.println();
            System.out.println(
                    "[" + getName()
                    + "] ─── iтерацiя "
                    + i
                    + " ───"
            );

            /*
             * Захоплення ресурсів
             */
            for (Resource resource : resources) {

                resource.acquire(getName());

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            /*
             * Імітація роботи процесу
             */
            try {
                Thread.sleep(700);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            /*
             * Звільнення ресурсів
             */
            for (Resource resource : resources) {

                resource.release(getName());

                try {
                    Thread.sleep(500);
                }
                catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        System.out.println();
        System.out.println(
                "[" + getName()
                + "] ✓ завершив роботу."
        );
    }
}

/*
 * Головний клас
 */
public class Main {

    public static void main(String[] args) {

        System.out.println();
        System.out.println(
                "════════════════════════════════════"
        );

        System.out.println(
                "Симуляцiю запущено"
        );

        System.out.println(
                "3 процеси, 2 ресурси"
        );

        System.out.println(
                "════════════════════════════════════"
        );

        /*
         * Створення ресурсів
         */
        Resource resource1 = new Resource(1);
        Resource resource2 = new Resource(2);

        /*
         * Процес 1 -> ресурс 1
         */
        ProcessThread process1 =
                new ProcessThread(
                        "Процес 1",
                        5,
                        resource1
                );

        /*
         * Процес 2 -> ресурс 2
         */
        ProcessThread process2 =
                new ProcessThread(
                        "Процес 2",
                        5,
                        resource2
                );

        /*
         * Процес 3 -> ресурс 1, 2
         */
        ProcessThread process3 =
                new ProcessThread(
                        "Процес 3",
                        5,
                        resource1,
                        resource2
                );

        /*
         * Запуск потоків
         */
        process1.start();
        process2.start();
        process3.start();

        /*
         * Очікування завершення потоків
         */
        try {

            process1.join();
            process2.join();
            process3.join();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println();
        System.out.println(
                "════════════════════════════════════"
        );

        System.out.println(
                "✓ Симуляцiю завершено!"
        );

        System.out.println(
                "════════════════════════════════════"
        );
    }
}