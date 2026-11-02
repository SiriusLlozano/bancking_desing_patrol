package banco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidarRetiroResponse {
    private String numeroCuenta;
    private double monto;
    private double saldoResultante;
}