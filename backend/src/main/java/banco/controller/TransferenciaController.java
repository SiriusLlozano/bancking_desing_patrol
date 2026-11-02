package banco.controller;

import banco.dto.TransferenciaRequest;
import banco.dto.TransferenciaResponse;
import banco.service.MovimientoService;
import banco.service.TokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transferencias")
@CrossOrigin(origins = "http://localhost:5173")
public class TransferenciaController {
    private final MovimientoService movimientoService;
    private final TokenService tokenService;

    public TransferenciaController(MovimientoService movimientoService, TokenService tokenService) {
        this.movimientoService = movimientoService;
        this.tokenService = tokenService;
    }

    @PostMapping
    public ResponseEntity<TransferenciaResponse> transferir(@RequestHeader("Authorization") String authorization,
            @Valid @RequestBody TransferenciaRequest req) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.status(HttpStatus.CREATED).body(movimientoService.transferir(req, usuarioId));
    }
}