package banco.domain.cuenta;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ReglaAutorizacionRetiroTest {
    @Test
    void autorizacionInmediataNoRequiereFirmasAdicionales() {
        ReglaAutorizacionRetiro regla = new AutorizacionInmediata();

        assertEquals(0, regla.aprobacionesRequeridas(1));
        assertEquals(0, regla.aprobacionesRequeridas(3));
    }

    @Test
    void autorizacionConjuntaRequiereAprobacionDeCadaOtroTitular() {
        ReglaAutorizacionRetiro regla = new AutorizacionTodosLosTitulares();

        assertEquals(0, regla.aprobacionesRequeridas(1));
        assertEquals(1, regla.aprobacionesRequeridas(2));
        assertEquals(2, regla.aprobacionesRequeridas(3));
    }
}
