package banco.controller;

import banco.dto.MovimientoRequest;
import banco.dto.MovimientoResponse;
import banco.service.MovimientoService;
import banco.service.TokenService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cuentas/{numero}/movimientos")
@CrossOrigin(origins = "http://localhost:5173")
public class MovimientoController {
    private final MovimientoService movimientoService;
    private final TokenService tokenService;

    public MovimientoController(MovimientoService movimientoService, TokenService tokenService) {
        this.movimientoService = movimientoService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<MovimientoResponse> registrar(@RequestHeader("Authorization") String authorization,
            @PathVariable String numero,
            @Valid @RequestBody MovimientoRequest req) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.registrar(numero, usuarioId, req));
    }

    @GetMapping
    public ResponseEntity<List<MovimientoResponse>> listar(@RequestHeader("Authorization") String authorization,
            @PathVariable String numero) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(movimientoService.listar(numero, usuarioId));
    }
}