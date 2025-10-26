// Archivo: PriorityNPScheduler.java
package proyecto1so;

public class PriorityNPScheduler implements Scheduler {
    @Override
    public PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick) {
        PCB[] arr = listos.toArray();
        if (arr.length == 0) return null;
        PCB best = null; int bestPrio = Integer.MAX_VALUE;
        for (PCB p : arr) {
            if (p == null) continue;
            if (p.getPrioridad() < bestPrio) { bestPrio = p.getPrioridad(); best = p; }
        }
        if (best != null) listos.remover(best);
        return best;
    }
}
