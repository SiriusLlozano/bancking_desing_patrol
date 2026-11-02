package banco.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class TokenService {
    private final Map<String, Long> usuariosPorToken = new ConcurrentHashMap<>();

    public void registrar(String token, Long usuarioId) {
        usuariosPorToken.put(token, usuarioId);
    }

    public Long obtenerUsuarioId(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión no válida");
        }
        Long usuarioId = usuariosPorToken.get(authorization.substring(7));
        if (usuarioId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Sesión vencida; inicia sesión otra vez");
        }
        return usuarioId;
    }

    public void invalidar(String authorization) {
        obtenerUsuarioId(authorization);
        usuariosPorToken.remove(authorization.substring(7));
    }
}