package banco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {
    @NotBlank private String username;
    @NotBlank private String password;
    @NotBlank private String nombre;
}