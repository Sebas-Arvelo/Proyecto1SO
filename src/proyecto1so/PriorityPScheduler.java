// Archivo: PriorityPScheduler.java
package proyecto1so;

public class PriorityPScheduler implements Scheduler {
    @Override public boolean isPreemptive() { return true; }

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

    @Override
    public boolean shouldPreempt(ColaProcesos listos, PCB actual, long tick) {
        if (actual == null) return false;
        int prio = actual.getPrioridad();
        for (PCB p : listos.toArray()) {
            if (p == null) continue;
            if (p.getPrioridad() < prio) return true;
        }
        return false;
    }
}
