// Archivo: SRTFScheduler.java
package proyecto1so;

public class SRTFScheduler implements Scheduler {
    @Override public boolean isPreemptive() { return true; }

    @Override
    public PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick) {
        PCB[] arr = listos.toArray();
        if (arr.length == 0) return null;
        PCB best = null; int bestRem = Integer.MAX_VALUE;
        for (PCB p : arr) {
            if (p == null) continue;
            int rem = p.getTotalInstrucciones() - p.getProgramCounter();
            if (rem < bestRem) { bestRem = rem; best = p; }
        }
        if (best != null) listos.remover(best);
        return best;
    }

    @Override
    public boolean shouldPreempt(ColaProcesos listos, PCB actual, long tick) {
        if (actual == null) return false;
        int remActual = actual.getTotalInstrucciones() - actual.getProgramCounter();
        for (PCB p : listos.toArray()) {
            if (p == null) continue;
            int rem = p.getTotalInstrucciones() - p.getProgramCounter();
            if (rem < remActual) return true;
        }
        return false;
    }
}
