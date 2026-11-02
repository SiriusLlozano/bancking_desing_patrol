package banco.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    @NotBlank(message = "Usuario requerido")
    private String username;
    @NotBlank(message = "Password requerido")
    private String password;
}