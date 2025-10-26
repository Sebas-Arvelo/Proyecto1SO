// Archivo: PCB.java
package proyecto1so;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Process Control Block (PCB).
 * Contiene toda la información vital de un proceso.
 */
public class PCB {
    
    // --- Atributos de Identificación ---
    private static final AtomicInteger CONTADOR_ID = new AtomicInteger(0); // Para generar IDs únicos de forma thread-safe
    private int id;                 // ID único [cite: 51]
    private String nombre;          // Nombre [cite: 53]
    private EstadoProceso estado;   // Estado (Listo, Bloqueado, etc.) [cite: 52]
    
    // --- Atributos de CPU (Registros) ---
    private int programCounter;     // PC [cite: 54]
    private int memoryAddressRegister; // MAR [cite: 58]
    
    // --- Atributos de Planificación ---
    private int totalInstrucciones; // Longitud del programa [cite: 71]
    private TipoProceso tipo;       // CPU_BOUND o IO_BOUND
    private int prioridad;          // menor número = mayor prioridad
    
    // --- Atributos de E/S (Excepciones) ---
    private int ciclosParaSolicitudIO;  // Cada cuántos ciclos pide E/S [cite: 73]
    private int ciclosParaCompletarIO;  // Cuántos ciclos tarda la E/S [cite: 74]
    private int contadorCiclosIO; // Contador interno para IO_BOUND
    
    // --- Campos adicionales (simulación y métricas) ---
    private int ioRestante; // ciclos restantes para completar la E/S actual
    private long tickLlegada;        // momento de llegada al sistema
    private long tickPrimerDespacho; // primer tick en CPU
    private long tickFinalizacion;   // tick de terminación
    private long tiempoEsperaAcum;   // ticks en colas de listo/suspendido listo
    private long tiempoCPUAcum;      // ticks ejecutados en CPU
    private long tiempoBloqueadoAcum;// ticks en bloqueo/suspendido bloqueado

    /**
     * Constructor para crear un nuevo proceso.
     */
    public PCB(String nombre, int totalInstrucciones, String tipo, 
               int ciclosParaSolicitudIO, int ciclosParaCompletarIO) {
        
        // Validaciones básicas
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proceso no puede ser nulo o vacío");
        }
        if (totalInstrucciones <= 0) {
            throw new IllegalArgumentException("El total de instrucciones debe ser mayor que 0");
        }
        if (!("CPU_BOUND".equals(tipo) || "IO_BOUND".equals(tipo))) {
            throw new IllegalArgumentException("El tipo debe ser 'CPU_BOUND' o 'IO_BOUND'");
        }
        if (ciclosParaSolicitudIO < 0 || ciclosParaCompletarIO < 0) {
            throw new IllegalArgumentException("Los ciclos de E/S no pueden ser negativos");
        }
        
        this.id = CONTADOR_ID.incrementAndGet(); // Asigna un ID único incremental, seguro para hilos
        this.nombre = nombre.trim();
        this.totalInstrucciones = totalInstrucciones;
        this.tipo = "CPU_BOUND".equals(tipo) ? TipoProceso.CPU_BOUND : TipoProceso.IO_BOUND;
        this.ciclosParaSolicitudIO = ciclosParaSolicitudIO;
        this.ciclosParaCompletarIO = ciclosParaCompletarIO;
        
        // Valores iniciales
        this.estado = EstadoProceso.NUEVO;
        this.programCounter = 0;
        this.memoryAddressRegister = 0; // Asumimos que empieza en 0
        this.contadorCiclosIO = 0;
        this.ioRestante = 0;
        this.tickLlegada = 0;
        this.tickPrimerDespacho = -1;
        this.tickFinalizacion = -1;
        this.tiempoEsperaAcum = 0;
        this.tiempoCPUAcum = 0;
        this.tiempoBloqueadoAcum = 0;
        this.prioridad = 5; // por defecto
    }

    /**
     * Simula la ejecución de un ciclo de instrucción en el CPU.
     * Incrementa el PC y el MAR.
     */
    public void ejecutarCiclo() {
        this.programCounter++;
        this.memoryAddressRegister++; // Asumiendo ejecución lineal [cite: 86]
        
        // Si es I/O bound, también incrementa su contador interno
        if (esIOBound()) {
            this.contadorCiclosIO++;
        }
    }
    
    /**
     * Verifica si el proceso debe solicitar una E/S en este ciclo.
     * @return true si debe pasar a BLOQUEADO, false en caso contrario.
     */
    public boolean debeSolicitarIO() {
        // Solo solicita E/S si es I/O Bound y le toca
        if (esIOBound() && 
            this.contadorCiclosIO >= this.ciclosParaSolicitudIO) {
            
            this.contadorCiclosIO = 0; // Resetea el contador para la próxima E/S
            return true;
        }
        return false;
    }
    
    // --- Ayudas de tipo ---
    public boolean esIOBound() { return tipo == TipoProceso.IO_BOUND; }
    public boolean esCPUBound() { return tipo == TipoProceso.CPU_BOUND; }

    /**
     * Verifica si el proceso ha completado todas sus instrucciones.
     * @return true si debe pasar a TERMINADO, false en caso contrario.
     */
    public boolean haTerminado() {
        return this.programCounter >= this.totalInstrucciones;
    }

    // --- Utilidades de métricas ---
    public void marcarLlegada(long tick) { this.tickLlegada = tick; }
    public void marcarDespachoSiPrimeraVez(long tick) {
        if (this.tickPrimerDespacho < 0) this.tickPrimerDespacho = tick;
    }
    public void marcarFinalizacion(long tick) { this.tickFinalizacion = tick; }
    public void incEspera() { this.tiempoEsperaAcum++; }
    public void incCPU() { this.tiempoCPUAcum++; }
    public void incBloqueado() { this.tiempoBloqueadoAcum++; }

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
    public void setProgramCounter(int programCounter) { this.programCounter = programCounter; }
    public int getMemoryAddressRegister() { return memoryAddressRegister; }
    public void setMemoryAddressRegister(int memoryAddressRegister) { this.memoryAddressRegister = memoryAddressRegister; }
    public int getTotalInstrucciones() { return totalInstrucciones; }
    public String getTipo() { return tipo.name(); }
    public int getCiclosParaSolicitudIO() { return ciclosParaSolicitudIO; }
    public int getCiclosParaCompletarIO() { return ciclosParaCompletarIO; }
    public int getContadorCiclosIO() { return contadorCiclosIO; }
    public void setContadorCiclosIO(int contadorCiclosIO) { this.contadorCiclosIO = contadorCiclosIO; }

    // --- Prioridad ---
    public int getPrioridad() { return prioridad; }
    public void setPrioridad(int prioridad) { this.prioridad = Math.max(1, prioridad); }

    // --- IO restante ---
    public int getIoRestante() { return ioRestante; }
    public void setIoRestante(int ioRestante) { this.ioRestante = Math.max(0, ioRestante); }

    // --- Métricas ---
    public long getTickLlegada() { return tickLlegada; }
    public long getTickPrimerDespacho() { return tickPrimerDespacho; }
    public long getTickFinalizacion() { return tickFinalizacion; }
    public long getTiempoEsperaAcum() { return tiempoEsperaAcum; }
    public long getTiempoCPUAcum() { return tiempoCPUAcum; }
    public long getTiempoBloqueadoAcum() { return tiempoBloqueadoAcum; }
}
