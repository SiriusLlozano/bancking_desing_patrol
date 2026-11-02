package banco.config;

/**
 * ============================================================
 *  PATRON CREACIONAL: SINGLETON
 * ============================================================
 *
 * IDEA DEL PATRON:
 *   Garantizar que una clase tenga UNA SOLA instancia en toda la
 *   aplicacion y dar un punto de acceso global a ella.
 *
 * COMO SE LOGRA:
 *   1) El constructor es "private": nadie de afuera puede hacer "new".
 *   2) Guardamos la unica instancia en un atributo "static".
 *   3) El metodo "getInstancia()" la crea la primera vez y siempre
 *      devuelve la misma.
 */

public class ConfiguracionBanco {

    /**
     * La unica instancia de la clase. Es "static" porque pertenece a
     * la clase, no a un objeto. Empieza en null (no creada todavia).
     */
    private static ConfiguracionBanco instancia;

    private String nombreBanco;
    private String moneda;
    private double tasaInteresAhorros;
    private double limiteRetiroDiario;

    /**
     * Constructor PRIVATE: impide que se cree con "new ConfiguracionBanco()".
     * Aqui se cargan los valores por defecto del banco.
     */
    private ConfiguracionBanco() {
        this.nombreBanco = "Banco Sirbull";
        this.moneda = "PEN";
        this.tasaInteresAhorros = 0.04;   // 4% anual
        this.limiteRetiroDiario = 2000;
    }

    /**
     * Si la instancia no existe, la crea;
     * si ya existe, la reutiliza.
     */
    public static synchronized ConfiguracionBanco getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionBanco();
        }
        return instancia;
    }

    public String getNombreBanco() {
        return nombreBanco;
    }

    public void setNombreBanco(String nombreBanco) {
        this.nombreBanco = nombreBanco;
    }

    public String getMoneda() {
        return moneda;
    }

    public void setMoneda(String moneda) {
        this.moneda = moneda;
    }

    public double getTasaInteresAhorros() {
        return tasaInteresAhorros;
    }

    public void setTasaInteresAhorros(double tasaInteresAhorros) {
        this.tasaInteresAhorros = tasaInteresAhorros;
    }

    public double getLimiteRetiroDiario() {
        return limiteRetiroDiario;
    }

    public void setLimiteRetiroDiario(double limiteRetiroDiario) {
        this.limiteRetiroDiario = limiteRetiroDiario;
    }

    @Override
    public String toString() {
        return "ConfiguracionBanco{" +
                "nombreBanco='" + nombreBanco + '\'' +
                ", moneda='" + moneda + '\'' +
                ", tasaInteresAhorros=" + tasaInteresAhorros +
                ", limiteRetiroDiario=" + limiteRetiroDiario +
                '}';
    }
}
