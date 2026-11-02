package banco.domain.cuenta;

public class AutorizacionTodosLosTitulares implements ReglaAutorizacionRetiro {
    @Override
    public int aprobacionesRequeridas(int cantidadTitulares) {
        return Math.max(0, cantidadTitulares - 1);
    }
}
