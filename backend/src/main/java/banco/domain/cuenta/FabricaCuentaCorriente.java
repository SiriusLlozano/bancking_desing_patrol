package banco.domain.cuenta;

/** Creador concreto: fabrica Cuentas Corrientes. */
public class FabricaCuentaCorriente extends FabricaCuenta {

    private static final double LIMITE_SOBREGIRO = 500_000;

    @Override
    public Cuenta crearCuenta(String numero, double saldoInicial) {
        return new CuentaCorriente(numero, saldoInicial, LIMITE_SOBREGIRO);
    }
}