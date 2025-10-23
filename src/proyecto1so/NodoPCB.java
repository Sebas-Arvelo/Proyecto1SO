// Archivo: NodoPCB.java
package proyecto1so;

/**
 * Representa un "eslabón" o "vagón" en tu lista enlazada (ColaProcesos).
 * Esta clase contiene el PCB y las referencias al nodo siguiente y anterior.
 */
public class NodoPCB {
    
    private PCB proceso;        // La "carga" que lleva el vagón
    private NodoPCB siguiente;  // El gancho al vagón de adelante
    private NodoPCB anterior;   // El gancho al vagón de atrás

    /**
     * Constructor.
     * @param proceso El PCB (la "carga") que este nodo va a contener.
     */
    public NodoPCB(PCB proceso) {
        this.proceso = proceso;
        this.siguiente = null;
        this.anterior = null;
    }

    // --- Getters y Setters ---
    
    public PCB getProceso() {
        return proceso;
    }

    public void setProceso(PCB proceso) {
        this.proceso = proceso;
    }

    public NodoPCB getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(NodoPCB siguiente) {
        this.siguiente = siguiente;
    }

    public NodoPCB getAnterior() {
        return anterior;
    }

    public void setAnterior(NodoPCB anterior) {
        this.anterior = anterior;
    }
}