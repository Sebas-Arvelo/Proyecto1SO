// Archivo: Clock.java
package proyecto1so;

public class Clock extends Thread {
    private volatile boolean running = false;
    private volatile boolean paused = true;
    private volatile long tick = 0;
    private volatile int cycleMillis;
    private final Object pauseLock = new Object();
    private final Kernel kernel;

    public Clock(Kernel kernel, int cycleMillis) {
        this.kernel = kernel;
        this.cycleMillis = Math.max(1, cycleMillis);
        setName("Clock");
        setDaemon(true);
    }

    public long getTick() { return tick; }
    public int getCycleMillis() { return cycleMillis; }
    public void setCycleMillis(int ms) { this.cycleMillis = Math.max(1, ms); }

    public void startClock() {
        running = true; paused = false;
        if (!isAlive()) start();
        synchronized (pauseLock) { pauseLock.notifyAll(); }
    }

    public void pauseClock() { paused = true; }
    public void resumeClock() { paused = false; synchronized (pauseLock){ pauseLock.notifyAll(); } }
    public void stopClock() { running = false; resumeClock(); }

    @Override public void run() {
        while (running) {
            try {
                synchronized (pauseLock) {
                    while (paused && running) pauseLock.wait();
                }
                // Tick
                tick++;
                kernel.onTick(tick);
                Thread.sleep(cycleMillis);
            } catch (InterruptedException ie) {
                // ignore
            }
        }
    }
}
