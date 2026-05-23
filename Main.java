public class Main {

    // Спільний буфер (1 комірка)
    static class Buffer {
        private int value = -1;
        private boolean hasValue = false;

        // запис (Виробник)
        public synchronized void put(int v) {
            try {
                while (hasValue) {
                    wait();
                }

                value = v;
                hasValue = true;

                System.out.println("Виробник записує: " + v);

                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // читання (Споживач)
        public synchronized int get() {
            int result = 0;
            try {
                while (!hasValue) {
                    wait();
                }

                result = value;
                hasValue = false;

                System.out.println("Споживач зчитує: " + result);

                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    // Виробник
    static class Producer implements Runnable {
        private Buffer buffer;

        public Producer(Buffer buffer) {
            this.buffer = buffer;
            new Thread(this, "Виробник").start();
        }

        public void run() {
            for (int i = 1; i <= 4; i++) {
                buffer.put(i);

                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }

            System.out.println("Виробник завершив генерацію.");
            System.out.println("Завершення потоку Виробник.");
        }
    }

    // Споживач
    static class Consumer implements Runnable {
        private Buffer buffer;

        public Consumer(Buffer buffer) {
            this.buffer = buffer;
            new Thread(this, "Споживач").start();
        }

        public void run() {
            int sum = 0;

            for (int i = 1; i <= 4; i++) {
                int val = buffer.get();
                sum += val;

                try { Thread.sleep(50); } catch (InterruptedException e) {}
            }

            System.out.println("Споживач сума: " + sum);
            System.out.println("Завершення потоку Споживач.");
        }
    }

    // MAIN
    public static void main(String[] args) {

        System.out.println("Початковий стан буфера порожній");

        Buffer buffer = new Buffer();

        new Producer(buffer);
        new Consumer(buffer);
    }
}