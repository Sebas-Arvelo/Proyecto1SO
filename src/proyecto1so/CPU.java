// Archivo: CPU.java
package proyecto1so;

public class CPU extends Thread {
    // Arreglo: faltaba el nombre de la variable
    private final Kernel kernel;
    private volatile PCB actual;
    private volatile boolean running = true;
    private long lastTick = -1; // ejecutar una vez por tick

    public CPU(Kernel kernel) {
        this.kernel = kernel;
        setName("CPU");
        setDaemon(true);
    }

    public PCB getActual() { return actual; }

    @Override public void run() {
        while (running) {
            try {
                Thread.sleep(1);
                long tick = kernel.getClock().getTick();
                if (tick == lastTick) {
                    continue; // esperar siguiente tick
                }
                lastTick = tick;

                // Selección segura del siguiente proceso
                if (actual == null) {
                    try { kernel.getMutex().acquire();
                        actual = kernel.getScheduler().seleccionarSiguiente(kernel.getColaListos(), null, tick);
                        if (actual != null) {
                            actual.setEstado(EstadoProceso.EJECUCION);
                            actual.marcarDespachoSiPrimeraVez(tick);
                            kernel.getScheduler().onDispatch(actual, tick);
                        }
                    } finally { kernel.getMutex().release(); }
                }

                if (actual != null) {
                    // Ejecutar un ciclo (una vez por tick)
                    actual.ejecutarCiclo();
                    actual.incCPU();
                    kernel.getScheduler().onTick(actual, tick);

                    // ¿terminó?
                    if (actual.haTerminado()) {
                        kernel.terminar(actual);
                        actual = null;
                    } else if (actual.debeSolicitarIO()) {
                        // ¿excepción IO?
                        kernel.bloquearPorIO(actual);
                        actual = null;
                    } else if (kernel.getScheduler().isPreemptive()) {
                        // ¿expropiar?
                        boolean preempt;
                        try { kernel.getMutex().acquire();
                            preempt = kernel.getScheduler().shouldPreempt(kernel.getColaListos(), actual, tick);
                        } finally { kernel.getMutex().release(); }
                        if (preempt) {
                            try { kernel.getMutex().acquire();
                                actual.setEstado(EstadoProceso.LISTO);
                                kernel.getColaListos().encolar(actual);
                                kernel.getScheduler().onEnqueue(kernel.getColaListos());
                            } finally { kernel.getMutex().release(); }
                            actual = null;
                        }
                    }
                }

                // Incrementar tiempos de espera/bloqueo en colas (una vez por tick)
                incrementarTiemposColas();
            } catch (InterruptedException ie) {
                running = false;
            }
        }
    }

    private void incrementarTiemposColas() {
        try { kernel.getMutex().acquire();
            // Recorre colas y actualiza acumulados. Sin colecciones, usamos toArray existente
            for (PCB p : kernel.getColaListos().toArray()) p.incEspera();
            for (PCB p : kernel.getColaBloqueados().toArray()) p.incBloqueado();
            for (PCB p : kernel.getColaListosSusp().toArray()) p.incEspera();
            for (PCB p : kernel.getColaBloqSusp().toArray()) p.incBloqueado();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally { kernel.getMutex().release(); }
    }
}
