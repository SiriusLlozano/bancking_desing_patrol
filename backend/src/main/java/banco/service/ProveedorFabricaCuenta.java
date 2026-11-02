package banco.service;

import banco.domain.cuenta.FabricaCuenta;
import banco.domain.cuenta.FabricaCuentaAhorros;
import banco.domain.cuenta.FabricaCuentaCorriente;
import banco.domain.cuenta.FabricaCuentaEmpresarial;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class ProveedorFabricaCuenta {

    public FabricaCuenta obtener(String tipo) {
        return switch (tipo.trim().toUpperCase(Locale.ROOT)) {
            case "AHORROS" -> new FabricaCuentaAhorros();
            case "CORRIENTE" -> new FabricaCuentaCorriente();
            case "EMPRESARIAL" -> new FabricaCuentaEmpresarial();
            default -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Tipo de cuenta inválido");
        };
    }
}