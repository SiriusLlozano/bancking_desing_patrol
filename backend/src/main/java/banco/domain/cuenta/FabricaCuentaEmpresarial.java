package banco.domain.cuenta;

/** Creador concreto: fabrica Cuentas Empresariales. */
public class FabricaCuentaEmpresarial extends FabricaCuenta {

    private static final double COMISION_RETIRO = 3_500;

    @Override
    public Cuenta crearCuenta(String numero, double saldoInicial) {
        return new CuentaEmpresarial(numero, saldoInicial, COMISION_RETIRO);
    }
}