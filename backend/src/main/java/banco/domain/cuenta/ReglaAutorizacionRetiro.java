package banco.domain.cuenta;

public interface ReglaAutorizacionRetiro {
    int aprobacionesRequeridas(int cantidadTitulares);
}
