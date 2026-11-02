package banco.domain.cuenta;

/** Cuenta de Ahorros con aplicación de intereses. */
public class CuentaAhorros extends Cuenta {

    private final double tasaInteresAnual;

    public CuentaAhorros(String numero, double saldoInicial, double tasaInteresAnual) {
        super(numero, saldoInicial);
        this.tasaInteresAnual = tasaInteresAnual;
    }

    @Override
    public String getTipo() {
        return "Cuenta de Ahorros";
    }

    public void aplicarInteresMensual() {
        saldo += saldo * (tasaInteresAnual / 12);
    }

    public double getTasaInteresAnual() {
        return tasaInteresAnual;
    }
}