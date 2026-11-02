package banco.domain.cuenta;

/** Cuenta corriente que permite sobregiro hasta un límite definido. */
public class CuentaCorriente extends Cuenta {

    private final double limiteSobregiro;

    public CuentaCorriente(String numero, double saldoInicial, double limiteSobregiro) {
        super(numero, saldoInicial);
        this.limiteSobregiro = limiteSobregiro;
    }

    @Override
    public String getTipo() {
        return "Cuenta Corriente";
    }

    @Override
    public boolean retirar(double monto) {
        if (monto <= 0 || (saldo - monto) < -limiteSobregiro) {
            return false;
        }
        saldo -= monto;
        return true;
    }

    public double getLimiteSobregiro() {
        return limiteSobregiro;
    }
}