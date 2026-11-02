package banco.domain.cuenta;

public class AutorizacionInmediata implements ReglaAutorizacionRetiro {
    @Override
    public int aprobacionesRequeridas(int cantidadTitulares) {
        return 0;
    }
}
