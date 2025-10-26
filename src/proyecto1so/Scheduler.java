// Archivo: Scheduler.java
package proyecto1so;

public interface Scheduler {
    // ¿es expropiativo?
    default boolean isPreemptive() { return false; }

    // Seleccionar siguiente proceso de cola de listos; puede considerar actual (para RR)
    PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick);

    // Notificar que un proceso fue encolado (para reordenar)
    default void onEnqueue(ColaProcesos listos) { }

    // Notificar que un proceso fue despachado a CPU (reiniciar contadores de quantum, etc.)
    default void onDispatch(PCB actual, long tick) { }

    // Notificar un tick de CPU para el proceso actual (incrementar contadores)
    default void onTick(PCB actual, long tick) { }

    // Indicar si debe expropiarse el proceso actual considerando la cola de listos
    default boolean shouldPreempt(ColaProcesos listos, PCB actual, long tick) { return false; }
}
