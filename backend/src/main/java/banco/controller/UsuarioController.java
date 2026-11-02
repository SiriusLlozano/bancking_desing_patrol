package banco.controller;

import banco.dto.UsuarioResumenResponse;
import banco.repository.UsuarioRepository;
import banco.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "http://localhost:5173")
public class UsuarioController {
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public UsuarioController(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    @GetMapping("/titulares")
    public ResponseEntity<List<UsuarioResumenResponse>> listarTitularesDisponibles(
            @RequestHeader("Authorization") String authorization) {
        Long usuarioId = tokenService.obtenerUsuarioId(authorization);
        return ResponseEntity.ok(usuarioRepository.findAll().stream()
                .filter(usuario -> !usuario.getId().equals(usuarioId))
                .filter(usuario -> "USER".equalsIgnoreCase(usuario.getRol()))
                .map(usuario -> new UsuarioResumenResponse(
                        usuario.getId(), usuario.getNombre(), usuario.getUsername()))
                .toList());
    }
}