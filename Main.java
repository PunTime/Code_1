/*
 * Варіант 12
 *
 * Кількість процесів: 3
 * Кількість ресурсів: 2
 *
 * Процес 1 -> ресурс 1, 2
 * Процес 2 -> ресурс 2
 * Процес 3 -> ресурс 1, 2
 */
 
class Resource {
 
    private final int id;
    private boolean busy = false;
 
    public Resource(int id) {
        this.id = id;
    }
 
    public synchronized void acquire(String processName) {
        while (busy) {
            try {
                System.out.println(
                    "  [" + processName + "] очiкує Ресурс " + id + "..."
                );
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        busy = true;
        System.out.println(
            "  [" + processName + "] >>> захопив Ресурс " + id
        );
    }
 
    public synchronized void release(String processName) {
        busy = false;
        System.out.println(
            "  [" + processName + "] <<< звiльнив Ресурс " + id
        );
        notifyAll();
    }
}
 
class ProcessThread extends Thread {
 
    private final Resource[] resources;
    private final int iterations;
 
    public ProcessThread(String name, int iterations, Resource... resources) {
        super(name);
        this.iterations = iterations;
        this.resources = resources;
    }
 
    @Override
    public void run() {
        for (int i = 1; i <= iterations; i++) {
 
            System.out.println();
 
            // Захоплення ресурсів
            for (Resource resource : resources) {
                resource.acquire(getName());
                try { Thread.sleep(500); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
 
            // Імітація роботи
            try { Thread.sleep(700); }
            catch (InterruptedException e) { e.printStackTrace(); }
 
            // Звільнення ресурсів
            for (Resource resource : resources) {
                resource.release(getName());
                try { Thread.sleep(500); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
        }
 
        System.out.println();
        System.out.println("[" + getName() + "] ✓ завершив роботу.");
    }
}
 
public class Main {
 
    public static void main(String[] args) {
 
        System.out.println();
        System.out.println("════════════════════════════════════");
        System.out.println("Симуляцiю запущено");
        System.out.println("3 процеси, 2 ресурси");
        System.out.println("Варiант 12: Процес1->[1,2], Процес2->[2], Процес3->[1,2]");
        System.out.println("════════════════════════════════════");
 
        Resource resource1 = new Resource(1);
        Resource resource2 = new Resource(2);
 
        // Процес 1 -> ресурс 1, 2
        ProcessThread process1 = new ProcessThread("Процес 1", 5, resource1, resource2);
 
        // Процес 2 -> ресурс 2
        ProcessThread process2 = new ProcessThread("Процес 2", 5, resource2);
 
        // Процес 3 -> ресурс 1, 2
        ProcessThread process3 = new ProcessThread("Процес 3", 5, resource1, resource2);
 
        process1.start();
        process2.start();
        process3.start();
 
        try {
            process1.join();
            process2.join();
            process3.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 
        System.out.println();
        System.out.println("════════════════════════════════════");
        System.out.println("✓ Симуляцiю завершено!");
        System.out.println("════════════════════════════════════");
    }
}