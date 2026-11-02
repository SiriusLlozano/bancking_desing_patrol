package banco.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RetiroResponse {
    private Long id;
    private String codigo;
    private String numeroCuenta;
    private double monto;
    private LocalDateTime venceEn;
    private String estado;
    private String nombreSolicitante;
    private int aprobacionesRecibidas;
    private int aprobacionesRequeridas;
    private boolean puedeAprobar;
}