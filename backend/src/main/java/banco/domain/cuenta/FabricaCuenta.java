package banco.domain.cuenta;

import banco.config.LoggerAuditoria;

/**
 * ============================================================
 *  AVANCE 2 - PATRON CREACIONAL: FACTORY METHOD
 *  Creador
 * ============================================================
 */
public abstract class FabricaCuenta {

    /* El factory method permite que cada subclase elija el producto concreto. */
    public abstract Cuenta crearCuenta(String numero, double saldoInicial);

    public Cuenta abrirCuenta(String numero, double saldoInicial) {
        Cuenta cuenta = crearCuenta(numero, saldoInicial);
        LoggerAuditoria.getInstancia().registrar(
                "Cuenta abierta -> " + cuenta.getTipo() + " #" + cuenta.getNumero()
                        + " saldo inicial=" + cuenta.getSaldo());
        return cuenta;
    }
}