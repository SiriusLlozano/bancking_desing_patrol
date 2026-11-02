package banco.controller;

import banco.dto.CrearCuentaRequest;
import banco.dto.CuentaResponse;
import banco.service.CuentaService;
import banco.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/cuentas")
@CrossOrigin(origins = "http://localhost:5173")
public class CuentaController {
    private final CuentaService cuentaService;
    private final TokenService tokenService;

    public CuentaController(CuentaService cuentaService, TokenService tokenService) {
        this.cuentaService = cuentaService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<CuentaResponse> crearCuenta(@RequestHeader("Authorization") String authorization,
            @Valid @RequestBody CrearCuentaRequest req) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.status(201).body(cuentaService.crearCuenta(req, usuarioId));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listarCuentas(
            @RequestHeader("Authorization") String authorization) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(cuentaService.listarCuentas(usuarioId));
    }

    @GetMapping("/{numero}")
    public ResponseEntity<CuentaResponse> obtenerPorNumero(@RequestHeader("Authorization") String authorization,
            @PathVariable String numero) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(cuentaService.obtenerPorNumero(numero, usuarioId));
    }
}