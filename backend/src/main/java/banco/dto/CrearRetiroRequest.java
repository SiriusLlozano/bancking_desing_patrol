package banco.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CrearRetiroRequest {
    @Positive private double monto;
}