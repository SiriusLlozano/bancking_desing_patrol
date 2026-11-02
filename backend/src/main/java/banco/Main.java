package banco;

import banco.config.ConfiguracionBanco;
import banco.config.LoggerAuditoria;
import banco.domain.cuenta.Cuenta;
import banco.domain.cuenta.FabricaCuenta;
import banco.domain.cuenta.FabricaCuentaAhorros;
import banco.domain.cuenta.FabricaCuentaCorriente;
import banco.domain.cuenta.FabricaCuentaEmpresarial;

/**
 * Punto de entrada del proyecto Banking Sirbull.
 * Cada avance agrega su propia prueba sin borrar las anteriores.
 */
public class Main {

    public static void main(String[] args) {
        probarAvance1Singleton();
        probarAvance2FactoryMethod();
    }

    /** SIGLETON : comprueba que las instancias unicas son el mismo objeto. */
    private static void probarAvance1Singleton() {
        System.out.println("=== Banking Sirbull - Avance 1: Patron Singleton ===\n");

        ConfiguracionBanco config1 = ConfiguracionBanco.getInstancia();
        ConfiguracionBanco config2 = ConfiguracionBanco.getInstancia();
        System.out.println("Configuracion de la instancia 1: " + config1);
        System.out.println("config1 es el mismo objeto que config2? " + (config1 == config2));

        LoggerAuditoria log1 = LoggerAuditoria.getInstancia();
        LoggerAuditoria log2 = LoggerAuditoria.getInstancia();
        System.out.println("log1 es el mismo objeto que log2? " + (log1 == log2) + "\n");

        log1.registrar("Sistema bancario iniciado.");
        log2.registrar("Moneda configurada: " + config1.getMoneda());
        System.out.println();
    }

    /** AVANCE : abre cuentas usando fabricas, sin usar "new" directamente. */
    private static void probarAvance2FactoryMethod() {
        System.out.println("=== Banking Sirbull - Avance 2: Patron Factory Method ===\n");

        // El cliente trabaja con la clase abstracta FabricaCuenta.
        FabricaCuenta fabricaAhorros = new FabricaCuentaAhorros();
        FabricaCuenta fabricaCorriente = new FabricaCuentaCorriente();
        FabricaCuenta fabricaEmpresarial = new FabricaCuentaEmpresarial();

        Cuenta ahorros = fabricaAhorros.abrirCuenta("AH-001", 500_000);
        Cuenta corriente = fabricaCorriente.abrirCuenta("CC-001", 200_000);
        Cuenta empresarial = fabricaEmpresarial.abrirCuenta("EM-001", 1_000_000);

        System.out.println();

        // Comportamiento polimorfico: cada cuenta reacciona distinto al retiro.
        System.out.println("Retiro de 300000 en " + ahorros.getTipo() + ": " + ahorros.retirar(300_000));
        System.out.println("Retiro de 600000 en " + corriente.getTipo() + " (con sobregiro): "
                + corriente.retirar(600_000));
        System.out.println("Retiro de 100000 en " + empresarial.getTipo() + " (con comision): "
                + empresarial.retirar(100_000));

        System.out.println("\nEstado final:");
        System.out.println("  " + ahorros);
        System.out.println("  " + corriente);
        System.out.println("  " + empresarial);
    }
}
