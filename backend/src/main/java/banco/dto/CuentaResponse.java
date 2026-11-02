package banco.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CuentaResponse {
    private String numero;
    private String tipo;
    private double saldo;
    private String titularidad;
    private boolean requiereAprobacion;
    private List<TitularResponse> titulares;
}