package banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MovimientoRequest {
    @NotBlank private String tipo;
    @Positive private double monto;
}