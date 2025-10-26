// Archivo: RRScheduler.java
package proyecto1so;

public class RRScheduler implements Scheduler {
    private final int quantum;
    private int usado = 0;

    public RRScheduler(int quantum) { this.quantum = Math.max(1, quantum); }

    @Override public boolean isPreemptive() { return true; }

    @Override
    public PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick) {
        usado = 0; // al seleccionar uno nuevo, reinicia el quantum usado
        return listos.desencolar();
    }

    @Override public void onDispatch(PCB actual, long tick) { usado = 0; }

    @Override public void onTick(PCB actual, long tick) { usado++; }

    @Override public boolean shouldPreempt(ColaProcesos listos, PCB actual, long tick) { return usado >= quantum; }
}
