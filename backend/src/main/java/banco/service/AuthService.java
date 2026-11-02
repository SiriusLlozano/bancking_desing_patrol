package banco.service;

import banco.dto.LoginRequest;
import banco.dto.LoginResponse;
import banco.dto.UsuarioRequest;
import banco.model.Usuario;
import banco.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.UUID;

@Service
public class AuthService {
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public AuthService(UsuarioRepository usuarioRepository, TokenService tokenService) {
        this.usuarioRepository = usuarioRepository;
        this.tokenService = tokenService;
    }

    public Usuario registrar(UsuarioRequest req) {
        if (usuarioRepository.existsByUsername(req.getUsername())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El usuario ya existe");
        }
        Usuario u = new Usuario();
        u.setUsername(req.getUsername());
        u.setPassword(req.getPassword());
        u.setNombre(req.getNombre());
        u.setRol("USER");
        return usuarioRepository.save(u);
    }

    public LoginResponse login(LoginRequest req) {
        Usuario u = usuarioRepository.findByUsername(req.getUsername())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));
        if (!u.getPassword().equals(req.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }
        String token = UUID.randomUUID().toString();
        tokenService.registrar(token, u.getId());
        return new LoginResponse(token, u.getUsername(), u.getNombre());
    }

    public void cerrarSesion(String authorization) {
        tokenService.invalidar(authorization);
    }
}