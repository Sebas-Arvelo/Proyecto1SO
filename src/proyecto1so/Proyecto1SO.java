/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package proyecto1so;

import javax.swing.SwingUtilities;

/**
 *
 * @author Sebastian
 */
public class Proyecto1SO {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Cargar configuración
        ConfigManager cfg = new ConfigManager();
        cfg.load();

        // Política inicial desde config (normalizada)
        Scheduler sched;
        String polRaw = cfg.getPolicy();
        String pol = (polRaw == null ? "FCFS" : polRaw.trim().toUpperCase());
        int quantum = Math.max(1, cfg.getQuantum()); // asegura quantum válido
        int cycleMillis = Math.max(1, cfg.getCycleMillis()); // asegura ciclo válido

        switch (pol) {
            case "RR":
                sched = new RRScheduler(quantum);
                break;
            case "SJF":
                sched = new SJFScheduler();
                break;
            case "SRTF":
                sched = new SRTFScheduler();
                break;
            case "P-NP":
                sched = new PriorityNPScheduler();
                break;
            case "P-P":
                sched = new PriorityPScheduler();
                break;
            default:
                sched = new FCFSScheduler();
                break;
        }

        Kernel kernel = new Kernel(cycleMillis, sched);

        // (Sin semillas automáticas) — use el botón "Semillas Demo" en la UI para crearlas cuando desee

        // Adjuntar GUI Swing en el hilo de despacho de eventos
        SwingUtilities.invokeLater(() -> {
            SwingSimulatorUI ui = new SwingSimulatorUI(kernel);
            kernel.attachUI(ui);
        });

        // Guardar config al cerrar (opcional)
        Runtime.getRuntime().addShutdownHook(new Thread(cfg::save));
    }
    
}
