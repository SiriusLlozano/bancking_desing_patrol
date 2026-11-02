package banco.controller;

import banco.dto.CrearRetiroRequest;
import banco.dto.RetiroResponse;
import banco.dto.ValidarRetiroRequest;
import banco.dto.ValidarRetiroResponse;
import java.util.List;
import banco.service.RetiroService;
import banco.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class RetiroController {
    private final RetiroService retiroService;
    private final TokenService tokenService;

    public RetiroController(RetiroService retiroService, TokenService tokenService) {
        this.retiroService = retiroService;
        this.tokenService = tokenService;
    }

    @PostMapping("/api/cuentas/{numero}/retiros")
    public ResponseEntity<RetiroResponse> generarCodigo(@RequestHeader("Authorization") String authorization,
            @PathVariable String numero, @Valid @RequestBody CrearRetiroRequest req) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(retiroService.generarCodigo(numero, usuarioId, req));
    }

    @GetMapping("/api/retiros/solicitudes")
    public ResponseEntity<List<RetiroResponse>> listarSolicitudes(
            @RequestHeader("Authorization") String authorization) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(retiroService.listarSolicitudes(usuarioId));
    }

    @PostMapping("/api/retiros/{retiroId}/aprobar")
    public ResponseEntity<RetiroResponse> aprobar(@RequestHeader("Authorization") String authorization,
            @PathVariable Long retiroId) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(retiroService.aprobar(retiroId, usuarioId));
    }

    @PostMapping("/api/cajero/retiros/validar")
    public ResponseEntity<ValidarRetiroResponse> validarEnCajero(@Valid @RequestBody ValidarRetiroRequest req) {
        return ResponseEntity.ok(retiroService.validarEnCajero(req.getCodigo()));
    }
}