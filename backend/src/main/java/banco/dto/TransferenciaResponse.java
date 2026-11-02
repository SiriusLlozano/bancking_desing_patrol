package banco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferenciaResponse {
    private String numeroOrigen;
    private String numeroDestino;
    private double monto;
    private double saldoOrigen;
    private double saldoDestino;
}