// Archivo: PCB.java
package proyecto1so;

/**
 * Process Control Block (PCB).
 * Contiene toda la información vital de un proceso.
 */
public class PCB {
    
    // --- Atributos de Identificación ---
    private static int contadorId = 0; // Para generar IDs únicos
    private int id;                 // ID único [cite: 51]
    private String nombre;          // Nombre [cite: 53]
    private EstadoProceso estado;   // Estado (Listo, Bloqueado, etc.) [cite: 52]
    
    // --- Atributos de CPU (Registros) ---
    private int programCounter;     // PC [cite: 54]
    private int memoryAddressRegister; // MAR [cite: 58]
    
    // --- Atributos de Planificación ---
    private int totalInstrucciones; // Longitud del programa [cite: 71]
    private String tipo;            // "CPU_BOUND" o "IO_BOUND" [cite: 72]
    
    // --- Atributos de E/S (Excepciones) ---
    private int ciclosParaSolicitudIO;  // Cada cuántos ciclos pide E/S [cite: 73]
    private int ciclosParaCompletarIO;  // Cuántos ciclos tarda la E/S [cite: 74]
    private int contadorCiclosIO; // Contador interno para IO_BOUND

    /**
     * Constructor para crear un nuevo proceso.
     */
    public PCB(String nombre, int totalInstrucciones, String tipo, 
               int ciclosParaSolicitudIO, int ciclosParaCompletarIO) {
        
        this.id = ++contadorId; // Asigna un ID único incremental
        this.nombre = nombre;
        this.totalInstrucciones = totalInstrucciones;
        this.tipo = tipo;
        this.ciclosParaSolicitudIO = ciclosParaSolicitudIO;
        this.ciclosParaCompletarIO = ciclosParaCompletarIO;
        
        // Valores iniciales
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0; // Asumimos que empieza en 0
        this.contadorCiclosIO = 0;
    }

    /**
     * Simula la ejecución de un ciclo de instrucción en el CPU.
     * Incrementa el PC y el MAR.
     */
    public void ejecutarCiclo() {
        this.programCounter++;
        this.memoryAddressRegister++; // Asumiendo ejecución lineal [cite: 86]
        
        // Si es I/O bound, también incrementa su contador interno
        if ("IO_BOUND".equals(this.tipo)) {
            this.contadorCiclosIO++;
        }
    }
    
    /**
     * Verifica si el proceso debe solicitar una E/S en este ciclo.
     * @return true si debe pasar a BLOQUEADO, false en caso contrario.
     */
    public boolean debeSolicitarIO() {
        // Solo solicita E/S si es I/O Bound y le toca
        if ("IO_BOUND".equals(this.tipo) && 
            this.contadorCiclosIO >= this.ciclosParaSolicitudIO) {
            
            this.contadorCiclosIO = 0; // Resetea el contador para la próxima E/S
            return true;
        }
        return false;
    }
    
    /**
     * Verifica si el proceso ha completado todas sus instrucciones.
     * @return true si debe pasar a TERMINADO, false en caso contrario.
     */
    public boolean haTerminado() {
        return this.programCounter >= this.totalInstrucciones;
    }
    
    /**
     * Define cómo se mostrará el PCB en las listas (JList) de la GUI.
     * Es muy útil para depurar.
     */
    @Override
    public String toString() {
        // Ejemplo: "ID: 1 | P_Navegador | PC: 50/200"
        return "ID: " + id + " | " + nombre + " | PC: " + programCounter + "/" + totalInstrucciones;
    }

    // --- Getters y Setters (los más necesarios) ---
    
    public int getId() { return id; }
    public String getNombre() { return nombre; }
    public EstadoProceso getEstado() { return estado; }
    public void setEstado(EstadoProceso estado) { this.estado = estado; }
    public int getProgramCounter() { return programCounter; }
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public int getTotalInstrucciones() { return totalInstrucciones; }
    public int getCiclosParaCompletarIO() { return ciclosParaCompletarIO; }
}
