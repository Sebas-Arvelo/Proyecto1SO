// Archivo: EstadoProceso.java
package proyecto1so;

/**
 * Define los estados fijos en los que puede estar un proceso,
 * según el diagrama de 7 estados (incluyendo suspendidos).
 */
public enum EstadoProceso {
    NUEVO,
    LISTO,
    EJECUCION,
    BLOQUEADO,
    TERMINADO,
    LISTO_SUSPENDIDO,
    BLOQUEADO_SUSPENDIDO
}