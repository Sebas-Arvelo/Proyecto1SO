// Archivo: SJFScheduler.java
package proyecto1so;

public class SJFScheduler implements Scheduler {
    @Override
    public PCB seleccionarSiguiente(ColaProcesos listos, PCB actual, long tick) {
        PCB[] arr = listos.toArray();
        if (arr.length == 0) return null;
        PCB best = null;
        int bestRem = Integer.MAX_VALUE;
        for (int i = 0; i < arr.length; i++) {
            PCB p = arr[i];
            if (p == null) continue;
            int rem = p.getTotalInstrucciones() - p.getProgramCounter();
            if (rem < bestRem) {
                bestRem = rem;
                best = p;
            }
        }
        if (best != null) {
            listos.remover(best);
        }
        return best;
    }

    @Override
    public void onEnqueue(ColaProcesos listos) { /* SJF no reordena in-place */ }
}
