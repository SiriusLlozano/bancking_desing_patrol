package banco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ValidarRetiroRequest {
    @NotBlank private String codigo;
}