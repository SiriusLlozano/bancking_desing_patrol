package banco.domain.cuenta;

/** Creador concreto: fabrica Cuentas de Ahorros. */
public class FabricaCuentaAhorros extends FabricaCuenta {

    private static final double TASA_INTERES_ANUAL = 0.04;

    @Override
    public Cuenta crearCuenta(String numero, double saldoInicial) {
        return new CuentaAhorros(numero, saldoInicial, TASA_INTERES_ANUAL);
    }
}