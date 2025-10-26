// Archivo: Kernel.java
package proyecto1so;

import java.util.concurrent.Semaphore;

public class Kernel {
    private final Clock clock;
    private final CPU cpu;
    private Scheduler scheduler;
    private SimulatorUI ui;

    // Exclusión mutua
    private final Semaphore mutex = new Semaphore(1);

    // Colas del sistema
    private final ColaProcesos colaListos = new ColaProcesos();
    private final ColaProcesos colaBloqueados = new ColaProcesos();
    private final ColaProcesos colaListosSusp = new ColaProcesos();
    private final ColaProcesos colaBloqSusp = new ColaProcesos();
    private final ColaProcesos colaTerminados = new ColaProcesos();

    private long ticksCPUOcupada = 0;
    private boolean started = false;

    public Kernel(int cycleMillis, Scheduler scheduler) {
        this.scheduler = scheduler;
        this.cpu = new CPU(this);
        this.clock = new Clock(this, cycleMillis);
    }

    public void attachUI(SimulatorUI ui) { this.ui = ui; }

    public void start() {
        if (!started) {
            clock.startClock();
            cpu.start();
            started = true;
            log("Kernel iniciado | Policy=" + (scheduler!=null?scheduler.getClass().getSimpleName():"-") + " | cycle=" + clock.getCycleMillis() + "ms");
        } else {
            resume();
        }
    }
    public void pause() { clock.pauseClock(); log("Kernel en pausa"); }
    public void resume() { clock.resumeClock(); log("Kernel reanudado"); }
    public void stop() { clock.stopClock(); cpu.interrupt(); log("Kernel detenido"); }

    public synchronized void setScheduler(Scheduler s) { this.scheduler = s; log("Scheduler cambiado a " + s.getClass().getSimpleName()); }

    public Clock getClock() { return clock; }
    public CPU getCPU() { return cpu; }
    public Scheduler getScheduler() { return scheduler; }

    public ColaProcesos getColaListos() { return colaListos; }
    public ColaProcesos getColaBloqueados() { return colaBloqueados; }
    public ColaProcesos getColaListosSusp() { return colaListosSusp; }
    public ColaProcesos getColaBloqSusp() { return colaBloqSusp; }
    public ColaProcesos getColaTerminados() { return colaTerminados; }

    public Semaphore getMutex() { return mutex; }

    // Hook del reloj
    public void onTick(long tick) {
        // Ya no avanzamos IO por tick: ahora lo hacen hilos independientes de E/S
        // contabilizar uso CPU
        if (cpu.getActual() != null) ticksCPUOcupada++;
        // refrescar UI
        if (ui != null) ui.refresh();
    }

    // Crear proceso
    public PCB crearProceso(String nombre, int instrucciones, String tipo, int cadaIO, int durIO) {
        try {
            mutex.acquire();
            PCB p = new PCB(nombre, instrucciones, tipo, cadaIO, durIO);
            p.setEstado(EstadoProceso.NUEVO);
            p.marcarLlegada(clock.getTick());
            // pasa a listo
            p.setEstado(EstadoProceso.LISTO);
            colaListos.encolar(p);
            if (scheduler != null) scheduler.onEnqueue(colaListos);
            log("Proceso creado: " + p.toString());
            return p;
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            return null;
        } finally {
            mutex.release();
        }
    }

    // Mover proceso a bloqueado por IO y lanzar hilo de E/S
    public void bloquearPorIO(PCB p) {
        try {
            mutex.acquire();
            p.setEstado(EstadoProceso.BLOQUEADO);
            p.setIoRestante(p.getCiclosParaCompletarIO());
            colaBloqueados.encolar(p);
            log("Proceso a BLOQUEADO por E/S: P" + p.getId());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
        // Lanzar hilo que simula la E/S de este proceso
        new IOThread(this, p, p.getCiclosParaCompletarIO()).start();
    }

    // Desbloquear cuando IO finaliza
    public void desbloquear(PCB p) {
        try {
            mutex.acquire();
            p.setEstado(EstadoProceso.LISTO);
            colaListos.encolar(p);
            if (scheduler != null) scheduler.onEnqueue(colaListos);
            log("Proceso desbloqueado -> LISTO: P" + p.getId());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
    }

    // Terminar proceso
    public void terminar(PCB p) {
        try {
            mutex.acquire();
            p.setEstado(EstadoProceso.TERMINADO);
            p.marcarFinalizacion(clock.getTick());
            colaTerminados.encolar(p);
            log("Proceso TERMINADO: P" + p.getId());
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.release();
        }
    }

    // Métricas
    public long getTicksTotales() { return clock.getTick(); }
    public long getTicksCPUOcupada() { return ticksCPUOcupada; }
    public double getUtilizacionCPU() {
        long tot = clock.getTick();
        return tot == 0 ? 0.0 : (double) ticksCPUOcupada / (double) tot;
    }

    public void log(String msg) {
        System.out.println("[" + clock.getTick() + "] " + msg);
        if (ui != null) ui.log(msg);
    }
}
