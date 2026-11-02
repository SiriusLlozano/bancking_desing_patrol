package banco.domain.cuenta;

/**
 * ============================================================
 *  AVANCE 2 - PATRON CREACIONAL: FACTORY METHOD
 *  Producto
 * ============================================================
 * Esta es la clase base de todos los tipos de cuenta. La fabrica
 * (FabricaCuenta) devolvera objetos de este tipo, pero el objeto
 * real sera una subclase concreta (Ahorros, Corriente, Empresarial).
 */
public abstract class Cuenta {

    protected String numero;
    protected double saldo;
    private ReglaAutorizacionRetiro reglaAutorizacionRetiro = new AutorizacionInmediata();

    public Cuenta(String numero, double saldoInicial) {
        this.numero = numero;
        this.saldo = saldoInicial;
    }

    public void depositar(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto a depositar debe ser positivo.");
        }
        saldo += monto;
    }

    /**
     * Comportamiento comun. Las subclases que necesiten reglas
     * especiales (como el sobregiro) lo sobrescriben.
     */
    public boolean retirar(double monto) {
        if (monto <= 0 || monto > saldo) {
            return false;
        }
        saldo -= monto;
        return true;
    }

    
    public abstract String getTipo();

    public void configurarReglaAutorizacionRetiro(ReglaAutorizacionRetiro regla) {
        if (regla == null) {
            throw new IllegalArgumentException("La regla de autorización es obligatoria.");
        }
        this.reglaAutorizacionRetiro = regla;
    }

    public int aprobacionesRequeridas(int cantidadTitulares) {
        return reglaAutorizacionRetiro.aprobacionesRequeridas(cantidadTitulares);
    }

    public String getNumero() {
        return numero;
    }

    public double getSaldo() {
        return saldo;
    }

    @Override
    public String toString() {
        return getTipo() + "{numero='" + numero + "', saldo=" + saldo + "}";
    }
}