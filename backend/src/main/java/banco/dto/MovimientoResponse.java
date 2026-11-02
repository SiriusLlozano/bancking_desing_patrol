package banco.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MovimientoResponse {
    private Long id;
    private String numeroCuenta;
    private String tipo;
    private double monto;
    private double saldoResultante;
    private LocalDateTime fecha;
}