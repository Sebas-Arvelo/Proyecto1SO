// Archivo: IOThread.java
package proyecto1so;

public class IOThread extends Thread {
    private final Kernel kernel;
    private final PCB pcb;
    private final int ciclos;

    public IOThread(Kernel kernel, PCB pcb, int ciclos) {
        this.kernel = kernel;
        this.pcb = pcb;
        this.ciclos = Math.max(0, ciclos);
        setName("IO-P" + pcb.getId());
        setDaemon(true);
    }

    @Override
    public void run() {
        for (int i = 0; i < ciclos; i++) {
            try {
                Thread.sleep(kernel.getClock().getCycleMillis());
            } catch (InterruptedException e) {
                return;
            }
            // Decrementar ioRestante de forma segura
            try {
                kernel.getMutex().acquire();
                int rest = pcb.getIoRestante();
                if (rest > 0) pcb.setIoRestante(rest - 1);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            } finally {
                kernel.getMutex().release();
            }
        }
        // Retirar de bloqueados y pasar a listos
        try {
            kernel.getMutex().acquire();
            kernel.getColaBloqueados().remover(pcb);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        } finally {
            kernel.getMutex().release();
        }
        kernel.desbloquear(pcb);
    }
}
