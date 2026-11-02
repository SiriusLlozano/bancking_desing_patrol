package banco.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ============================================================
 *  AVANCE 1 - PATRON SINGLETON (segundo ejemplo)
 * ============================================================
 *
 * El registro de auditoria (log) tambien debe ser UNO solo para todo
 * el banco: todos los eventos deben quedar en la misma bitacora.
 * Es otro caso tipico de Singleton.
 */
public class LoggerAuditoria {

    private static LoggerAuditoria instancia;

    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private LoggerAuditoria() {
    }

    public static synchronized LoggerAuditoria getInstancia() {
        if (instancia == null) {
            instancia = new LoggerAuditoria();
        }
        return instancia;
    }

    /** Registra un evento con la fecha y hora actual. */
    public void registrar(String evento) {
        System.out.println("[AUDITORIA " + LocalDateTime.now().format(formato) + "] " + evento);
    }
}
