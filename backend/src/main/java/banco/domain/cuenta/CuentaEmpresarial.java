package banco.domain.cuenta;

/** Cuenta empresarial que cobra una comisión por cada retiro. */
public class CuentaEmpresarial extends Cuenta {

    private final double comisionRetiro;

    public CuentaEmpresarial(String numero, double saldoInicial, double comisionRetiro) {
        super(numero, saldoInicial);
        this.comisionRetiro = comisionRetiro;
    }

    @Override
    public String getTipo() {
        return "Cuenta Empresarial";
    }

    @Override
    public boolean retirar(double monto) {
        double total = monto + comisionRetiro;
        if (monto <= 0 || total > saldo) {
            return false;
        }
        saldo -= total;
        return true;
    }

    public double getComisionRetiro() {
        return comisionRetiro;
    }
}