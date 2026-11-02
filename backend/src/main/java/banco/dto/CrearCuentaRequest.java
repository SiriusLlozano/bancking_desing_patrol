package banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class CrearCuentaRequest {
    @NotBlank private String tipo; // AHORROS, CORRIENTE, EMPRESARIAL
    @Positive private double saldoInicial;
    private String titularidad;
    private List<Long> titularesIds;
    private boolean requiereAprobacion;
}