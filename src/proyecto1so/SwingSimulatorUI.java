// Archivo: SwingSimulatorUI.java
package proyecto1so;

import javax.swing.*;
import java.awt.*;

public class SwingSimulatorUI extends JFrame implements SimulatorUI {
    private final Kernel kernel;
    private final ConfigManager cfg = new ConfigManager();

    private final JList<PCB> listListos = new JList<>();
    private final JList<PCB> listBloqueados = new JList<>();
    private final JList<PCB> listListosSusp = new JList<>();
    private final JList<PCB> listBloqSusp = new JList<>();
    private final JList<PCB> listTerminados = new JList<>();

    private final JTextArea logArea = new JTextArea(8, 50);

    private final JLabel lblCPUProceso = new JLabel("CPU: (idle)");
    private final JLabel lblPC = new JLabel("PC: -");
    private final JLabel lblMAR = new JLabel("MAR: -");
    private final JLabel lblTick = new JLabel("Tick: 0");
    private final JLabel lblScheduler = new JLabel("Policy: FCFS");
    private final JLabel lblModo = new JLabel("Modo: SO");

    private final JComboBox<String> comboPolicy = new JComboBox<>(new String[]{"FCFS","RR","SJF","SRTF","P-NP","P-P"});
    private final JSpinner spinQuantum = new JSpinner(new SpinnerNumberModel(3,1,100,1));
    private final JSpinner spinCycle = new JSpinner(new SpinnerNumberModel(100,1,2000,10));

    private final JButton btnStart = new JButton("Start");
    private final JButton btnPause = new JButton("Pause");
    private final JButton btnResume = new JButton("Resume");
    private final JButton btnCrear = new JButton("Crear Proceso");
    private final JButton btnDemo = new JButton("Semillas Demo"); // nuevo botón

    public SwingSimulatorUI(Kernel kernel) {
        super("SO Simulator");
        this.kernel = kernel;
        cfg.load();
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel izquierdo: colas
        JPanel left = new JPanel(new GridLayout(5,1));
        left.add(makeListPanel("Listos", listListos));
        left.add(makeListPanel("Bloqueados", listBloqueados));
        left.add(makeListPanel("Listos Susp.", listListosSusp));
        left.add(makeListPanel("Bloq. Susp.", listBloqSusp));
        left.add(makeListPanel("Terminados", listTerminados));

        // Panel superior: CPU/estado
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(lblCPUProceso); top.add(new JLabel("  |  "));
        top.add(lblPC); top.add(new JLabel("  |  "));
        top.add(lblMAR); top.add(new JLabel("  |  "));
        top.add(lblTick); top.add(new JLabel("  |  "));
        top.add(lblScheduler); top.add(new JLabel("  |  "));
        top.add(lblModo);

        // Panel de control
        JPanel ctrl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ctrl.add(new JLabel("Policy:")); ctrl.add(comboPolicy);
        ctrl.add(new JLabel("Quantum:")); ctrl.add(spinQuantum);
        ctrl.add(new JLabel("Cycle (ms):")); ctrl.add(spinCycle);
        ctrl.add(btnStart); ctrl.add(btnPause); ctrl.add(btnResume); ctrl.add(btnCrear); ctrl.add(btnDemo); // agregar botón demo

        // Log
        logArea.setEditable(false);
        JScrollPane logScroll = new JScrollPane(logArea);

        JPanel right = new JPanel(new BorderLayout());
        right.add(top, BorderLayout.NORTH);
        right.add(ctrl, BorderLayout.CENTER);
        right.add(logScroll, BorderLayout.SOUTH);

        add(left, BorderLayout.WEST);
        add(right, BorderLayout.CENTER);

        wireActions();
        pack();
        setSize(1100, 650);
        setLocationRelativeTo(null);
        setVisible(true);

        // Estado inicial de controles
        spinCycle.setValue(kernel.getClock().getCycleMillis());
        if ("RR".equals(cfg.getPolicy())) comboPolicy.setSelectedItem("RR");
        else if ("SJF".equals(cfg.getPolicy())) comboPolicy.setSelectedItem("SJF");
        else if ("SRTF".equals(cfg.getPolicy())) comboPolicy.setSelectedItem("SRTF");
        else if ("P-NP".equals(cfg.getPolicy())) comboPolicy.setSelectedItem("P-NP");
        else if ("P-P".equals(cfg.getPolicy())) comboPolicy.setSelectedItem("P-P");
        else comboPolicy.setSelectedItem("FCFS");
        spinQuantum.setValue(cfg.getQuantum());
    }

    private JPanel makeListPanel(String title, JList<PCB> list) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        list.setCellRenderer(new DefaultListCellRenderer());
        p.add(new JScrollPane(list), BorderLayout.CENTER);
        return p;
    }

    private void wireActions() {
        btnStart.addActionListener(e -> kernel.start());
        btnPause.addActionListener(e -> kernel.pause());
        btnResume.addActionListener(e -> kernel.resume());
        btnCrear.addActionListener(e -> mostrarDialogoCrearProceso());
        btnDemo.addActionListener(e -> crearSemillasDemo()); // acción del botón demo

        comboPolicy.addActionListener(e -> {
            String sel = (String) comboPolicy.getSelectedItem();
            cfg.setPolicy(sel);
            switch (sel) {
                case "RR":
                    int q = (int) spinQuantum.getValue();
                    cfg.setQuantum(q);
                    kernel.setScheduler(new RRScheduler(q));
                    lblScheduler.setText("Policy: RR("+q+")");
                    break;
                case "SJF":
                    kernel.setScheduler(new SJFScheduler());
                    lblScheduler.setText("Policy: SJF");
                    break;
                case "SRTF":
                    kernel.setScheduler(new SRTFScheduler());
                    lblScheduler.setText("Policy: SRTF");
                    break;
                case "P-NP":
                    kernel.setScheduler(new PriorityNPScheduler());
                    lblScheduler.setText("Policy: Prioridad NP");
                    break;
                case "P-P":
                    kernel.setScheduler(new PriorityPScheduler());
                    lblScheduler.setText("Policy: Prioridad P");
                    break;
                default:
                    kernel.setScheduler(new FCFSScheduler());
                    lblScheduler.setText("Policy: FCFS");
            }
            cfg.save();
        });

        spinCycle.addChangeListener(e -> {
            int ms = (int) spinCycle.getValue();
            kernel.getClock().setCycleMillis(ms);
            cfg.setCycleMillis(ms);
            cfg.save();
            log("Cycle set to "+ms+" ms");
        });
    }

    private void mostrarDialogoCrearProceso() {
        JTextField nombre = new JTextField("Proc"+(int)(Math.random()*100));
        JSpinner instr = new JSpinner(new SpinnerNumberModel(30,1,1000,1));
        JComboBox<String> tipo = new JComboBox<>(new String[]{"CPU_BOUND","IO_BOUND"});
        JSpinner cadaIO = new JSpinner(new SpinnerNumberModel(10,0,500,1));
        JSpinner durIO = new JSpinner(new SpinnerNumberModel(5,0,500,1));
        JSpinner prio = new JSpinner(new SpinnerNumberModel(5,1,20,1));

        JPanel panel = new JPanel(new GridLayout(0,2));
        panel.add(new JLabel("Nombre:")); panel.add(nombre);
        panel.add(new JLabel("Instrucciones:")); panel.add(instr);
        panel.add(new JLabel("Tipo:")); panel.add(tipo);
        panel.add(new JLabel("Cada IO:")); panel.add(cadaIO);
        panel.add(new JLabel("Duración IO:")); panel.add(durIO);
        panel.add(new JLabel("Prioridad (1=alta):")); panel.add(prio);

        int res = JOptionPane.showConfirmDialog(this, panel, "Crear Proceso", JOptionPane.OK_CANCEL_OPTION);
        if (res == JOptionPane.OK_OPTION) {
            PCB p = kernel.crearProceso(nombre.getText(), (int)instr.getValue(), (String)tipo.getSelectedItem(), (int)cadaIO.getValue(), (int)durIO.getValue());
            p.setPrioridad((int) prio.getValue());
        }
    }

    // Nuevo: crear varias semillas de demostración rápidamente
    private void crearSemillasDemo() {
        PCB d1 = kernel.crearProceso("Demo-IO1", 150, "IO_BOUND", 8, 5);  d1.setPrioridad(3);
        PCB d2 = kernel.crearProceso("Demo-CPU1", 300, "CPU_BOUND", 0, 0); d2.setPrioridad(5);
        PCB d3 = kernel.crearProceso("Demo-IO2", 220, "IO_BOUND", 12, 15); d3.setPrioridad(4);
        PCB d4 = kernel.crearProceso("Demo-CPU2", 260, "CPU_BOUND", 0, 0); d4.setPrioridad(6);
        log("Semillas de demostración creadas");
    }

    @Override
    public void refresh() {
        SwingUtilities.invokeLater(() -> {
            listListos.setListData(kernel.getColaListos().toArray());
            listBloqueados.setListData(kernel.getColaBloqueados().toArray());
            listListosSusp.setListData(kernel.getColaListosSusp().toArray());
            listBloqSusp.setListData(kernel.getColaBloqSusp().toArray());
            listTerminados.setListData(kernel.getColaTerminados().toArray());

            long tick = kernel.getClock().getTick();
            lblTick.setText("Tick: "+tick+"  |  Util: "+String.format("%.0f%%", kernel.getUtilizacionCPU()*100));

            PCB a = kernel.getCPU().getActual();
            if (a != null) {
                lblCPUProceso.setText("CPU: P"+a.getId()+" ("+a.getNombre()+")");
                lblPC.setText("PC: "+a.getProgramCounter());
                lblMAR.setText("MAR: "+a.getMemoryAddressRegister());
                lblModo.setText("Modo: Usuario");
            } else {
                lblCPUProceso.setText("CPU: (idle)");
                lblPC.setText("PC: -");
                lblMAR.setText("MAR: -");
                lblModo.setText("Modo: SO");
            }
        });
    }

    @Override
    public void log(String msg) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("["+kernel.getClock().getTick()+"] "+msg+"\n");
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}
