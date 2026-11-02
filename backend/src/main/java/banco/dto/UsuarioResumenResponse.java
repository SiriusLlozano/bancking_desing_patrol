package banco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UsuarioResumenResponse {
    private Long id;
    private String nombre;
    private String username;
}
