/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1so;

/**
 *
 * @author Sebastian
 */
public class Proyecto1SO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Ejemplo básico de uso del sistema de procesos
        System.out.println("=== Sistema de Gestión de Procesos ===");
        
        // Crear algunos procesos de ejemplo
        PCB proceso1 = new PCB("Navegador", 100, "IO_BOUND", 10, 5);
        PCB proceso2 = new PCB("Calculadora", 50, "CPU_BOUND", 0, 0);
        PCB proceso3 = new PCB("Editor", 75, "IO_BOUND", 15, 8);
        
        // Crear una cola de procesos
        ColaProcesos colaListos = new ColaProcesos();
        
        // Encolar los procesos
        colaListos.encolar(proceso1);
        colaListos.encolar(proceso2);
        colaListos.encolar(proceso3);
        
        System.out.println("Procesos en cola: " + colaListos.getTamano());
        
        // Mostrar todos los procesos
        PCB[] procesos = colaListos.toArray();
        for (PCB p : procesos) {
            System.out.println(p.toString());
        }
        
        // Simular ejecución básica
        System.out.println("\n=== Simulación de Ejecución ===");
        while (!colaListos.estaVacia()) {
            PCB procesoActual = colaListos.desencolar();
            procesoActual.setEstado(EstadoProceso.EJECUCION);
            System.out.println("Ejecutando: " + procesoActual.toString());
            
            // Simular algunos ciclos de CPU
            for (int i = 0; i < 5 && !procesoActual.haTerminado(); i++) {
                procesoActual.ejecutarCiclo();
                if (procesoActual.debeSolicitarIO()) {
                    procesoActual.setEstado(EstadoProceso.BLOQUEADO);
                    System.out.println("  -> Proceso bloqueado por E/S");
                    break;
                }
            }
            
            if (procesoActual.haTerminado()) {
                procesoActual.setEstado(EstadoProceso.TERMINADO);
                System.out.println("  -> Proceso terminado");
            } else if (procesoActual.getEstado() != EstadoProceso.BLOQUEADO) {
                procesoActual.setEstado(EstadoProceso.LISTO);
                System.out.println("  -> Proceso devuelto a cola de listos");
            }
        }
        
        System.out.println("\nSimulación completada.");
    }
    
}
