// Archivo: IOManager.java
package proyecto1so;

public class IOManager {
    private final Kernel kernel;

    public IOManager(Kernel kernel) { this.kernel = kernel; }

    public void onTick() {
        // Decrementa IO restante de bloqueados; si llega a 0, mover a listos
        PCB[] arr = kernel.getColaBloqueados().toArray();
        for (int i = 0; i < arr.length; i++) {
            PCB p = arr[i];
            if (p == null) continue;
            int rest = p.getIoRestante();
            if (rest > 0) {
                p.setIoRestante(rest - 1);
                if (p.getIoRestante() == 0) {
                    // Sacar de bloqueados y pasar a listos
                    kernel.getColaBloqueados().remover(p);
                    kernel.desbloquear(p);
                }
            }
        }
    }
}
