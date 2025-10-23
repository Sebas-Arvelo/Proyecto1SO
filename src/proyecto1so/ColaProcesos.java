// Archivo: ColaProcesos.java
package proyecto1so;

/**
 * Implementación de una cola (Queue) usando una Lista Doblemente Enlazada.
 * Esta clase es necesaria porque no se permite usar java.util.Queue. 
 */
public class ColaProcesos {
    private NodoPCB cabeza; // El inicio de la cola (el que va a salir)
    private NodoPCB cola;   // El final de la cola (el último que entró)
    private int tamano;

    /**
     * Constructor de una cola vacía.
     */
    public ColaProcesos() {
        this.cabeza = null;
        this.cola = null;
        this.tamano = 0;
    }

    /**
     * Verifica si la cola no tiene elementos.
     * @return true si está vacía.
     */
    public boolean estaVacia() {
        return this.cabeza == null;
    }

    /**
     * Devuelve el número de elementos en la cola.
     * @return el tamaño.
     */
    public int getTamano() {
        return this.tamano;
    }

    /**
     * Agrega un proceso al final (cola) de la lista.
     * @param proceso El PCB a encolar.
     */
    public void encolar(PCB proceso) {
        NodoPCB nuevoNodo = new NodoPCB(proceso);
        
        if (estaVacia()) {
            // Si está vacía, es la cabeza y la cola
            this.cabeza = nuevoNodo;
            this.cola = nuevoNodo;
        } else {
            // Si no, se enlaza al final
            this.cola.setSiguiente(nuevoNodo);
            nuevoNodo.setAnterior(this.cola);
            this.cola = nuevoNodo; // El nuevo nodo es ahora la cola
        }
        this.tamano++;
    }

    /**
     * Saca el primer proceso (cabeza) de la lista.
     * @return El PCB que estaba en la cabeza, o null si la cola está vacía.
     */
    public PCB desencolar() {
        if (estaVacia()) {
            return null; // No hay nada que sacar
        }
        
        // Saca el nodo de la cabeza
        NodoPCB nodoRemovido = this.cabeza;
        
        if (this.cabeza == this.cola) {
            // Era el único nodo
            this.cabeza = null;
            this.cola = null;
        } else {
            // Hay más nodos. La nueva cabeza es la siguiente
            this.cabeza = nodoRemovido.getSiguiente();
            this.cabeza.setAnterior(null); // La nueva cabeza no tiene anterior
        }
        
        this.tamano--;
        nodoRemovido.setSiguiente(null); // Limpia la referencia
        return nodoRemovido.getProceso(); // Devuelve el PCB
    }
    
    /**
     * Convierte la cola en un Array de PCBs.
     * Esto es VITAL para poder mostrar la cola en un JList (la GUI). [cite: 43]
     * @return Un array de tipo PCB.
     */
    public PCB[] toArray() {
        PCB[] array = new PCB[this.tamano];
        NodoPCB actual = this.cabeza;
        int i = 0;
        
        // Recorre la lista enlazada y copia los PCBs al array
        while (actual != null) {
            array[i] = actual.getProceso();
            actual = actual.getSiguiente();
            i++;
        }
        return array;
    }
    
    /**
     * Busca y remueve un PCB específico de cualquier parte de la cola.
     * (Necesario para mover un proceso de Bloqueado -> Listo).
     * @param pcbBuscado El PCB que quieres remover.
     * @return El PCB removido, o null si no se encontró.
     */
    public PCB remover(PCB pcbBuscado) {
        if (estaVacia()) {
            return null;
        }

        NodoPCB actual = this.cabeza;
        
        // Itera hasta encontrar el PCB o llegar al final
        while (actual != null && actual.getProceso().getId() != pcbBuscado.getId()) {
            actual = actual.getSiguiente();
        }
        
        // Si no se encontró
        if (actual == null) {
            return null; 
        }
        
        // Si se encontró, hay que re-enlazar la lista
        
        // Caso 1: Es la cabeza
        if (actual == this.cabeza) {
            return desencolar(); // El método desencolar ya maneja esto
        }
        
        // Caso 2: Es la cola
        if (actual == this.cola) {
            this.cola = actual.getAnterior();
            this.cola.setSiguiente(null);
        } else {
            // Caso 3: Es un nodo intermedio
            NodoPCB anterior = actual.getAnterior();
            NodoPCB siguiente = actual.getSiguiente();
            
            anterior.setSiguiente(siguiente);
            siguiente.setAnterior(anterior);
        }
        
        this.tamano--;
        actual.setAnterior(null); // Limpia referencias
        actual.setSiguiente(null);
        return actual.getProceso();
    }
}
