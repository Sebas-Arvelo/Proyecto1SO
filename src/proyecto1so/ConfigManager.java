// Archivo: ConfigManager.java
package proyecto1so;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private final Properties props = new Properties();
    private final Path file = Paths.get("config.properties");

    // Claves
    private static final String K_CYCLE = "cycleMillis";
    private static final String K_QUANTUM = "quantum";
    private static final String K_DEF_INSTR = "def.instr";
    private static final String K_DEF_TIPO = "def.tipo"; // CPU_BOUND | IO_BOUND
    private static final String K_DEF_CADAIO = "def.cadaIO";
    private static final String K_DEF_DURIO = "def.durIO";
    private static final String K_DEF_PRIO = "def.prioridad";
    private static final String K_POLICY = "policy"; // FCFS|RR|SJF|SRTF|P-NP|P-P

    public void load() {
        if (Files.exists(file)) {
            try (FileInputStream in = new FileInputStream(file.toFile())) {
                props.load(in);
            } catch (IOException e) { /* ignore */ }
        }
        // Defaults si faltan
        props.putIfAbsent(K_CYCLE, "150");
        props.putIfAbsent(K_QUANTUM, "3");
        props.putIfAbsent(K_DEF_INSTR, "30");
        props.putIfAbsent(K_DEF_TIPO, "CPU_BOUND");
        props.putIfAbsent(K_DEF_CADAIO, "10");
        props.putIfAbsent(K_DEF_DURIO, "5");
        props.putIfAbsent(K_DEF_PRIO, "5");
        props.putIfAbsent(K_POLICY, "FCFS");
    }

    public void save() {
        try (FileOutputStream out = new FileOutputStream(file.toFile())) {
            props.store(out, "Simulator Config");
        } catch (IOException e) { /* ignore */ }
    }

    private int getInt(String k) { return Integer.parseInt(props.getProperty(k)); }
    private void setInt(String k, int v) { props.setProperty(k, Integer.toString(v)); }

    public int getCycleMillis() { return getInt(K_CYCLE); }
    public void setCycleMillis(int v) { setInt(K_CYCLE, v); }

    public int getQuantum() { return getInt(K_QUANTUM); }
    public void setQuantum(int v) { setInt(K_QUANTUM, v); }

    public int getDefInstr() { return getInt(K_DEF_INSTR); }
    public void setDefInstr(int v) { setInt(K_DEF_INSTR, v); }

    public String getDefTipo() {
        String v = props.getProperty(K_DEF_TIPO);
        if (v == null) return "CPU_BOUND";
        v = v.trim().toUpperCase();
        if (!("CPU_BOUND".equals(v) || "IO_BOUND".equals(v))) return "CPU_BOUND";
        return v;
    }

    public int getDefCadaIO() { return getInt(K_DEF_CADAIO); }
    public void setDefCadaIO(int v) { setInt(K_DEF_CADAIO, v); }

    public int getDefDurIO() { return getInt(K_DEF_DURIO); }
    public void setDefDurIO(int v) { setInt(K_DEF_DURIO, v); }

    public int getDefPrioridad() { return getInt(K_DEF_PRIO); }
    public void setDefPrioridad(int v) { setInt(K_DEF_PRIO, v); }

    public String getPolicy() {
        String v = props.getProperty(K_POLICY);
        if (v == null) return "FCFS";
        v = v.trim().toUpperCase();
        switch (v) {
            case "RR": case "SJF": case "SRTF": case "P-NP": case "P-P": case "FCFS":
                return v;
            default:
                return "FCFS";
        }
    }
    public void setPolicy(String v) { props.setProperty(K_POLICY, v); }
}
