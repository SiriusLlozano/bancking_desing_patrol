package banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferenciaRequest {
    @NotBlank private String numeroOrigen;
    @NotBlank private String numeroDestino;
    @Positive private double monto;
}