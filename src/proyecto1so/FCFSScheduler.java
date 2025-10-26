// Archivo: FCFSScheduler.java
package proyecto1so;

public class FCFSScheduler implements Scheduler {
    @Override
    public PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick) {
        return listos.desencolar();
    }

    @Override
    public void onEnqueue(ColaProcesos listos) {
        // FCFS mantiene orden de llegada, no reordenar
    }
}
