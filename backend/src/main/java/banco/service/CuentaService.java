package banco.service;

import banco.domain.cuenta.Cuenta;
import banco.domain.cuenta.FabricaCuenta;
import banco.dto.CrearCuentaRequest;
import banco.dto.CuentaResponse;
import banco.dto.TitularResponse;
import banco.model.CuentaEntity;
import banco.model.Usuario;
import banco.repository.CuentaRepository;
import banco.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.security.SecureRandom;
import java.util.List;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CuentaService {
    private static final String CODIGO_BANCO = "001";
    private static final long RANGO_NUMERO_CUENTA = 10_000_000_000_000_000L;

    private final CuentaRepository cuentaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorFabricaCuenta proveedorFabricaCuenta;
    private final SecureRandom random = new SecureRandom();

    public CuentaService(CuentaRepository cuentaRepository, UsuarioRepository usuarioRepository,
            ProveedorFabricaCuenta proveedorFabricaCuenta) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.proveedorFabricaCuenta = proveedorFabricaCuenta;
    }

    @Transactional
    public CuentaResponse crearCuenta(CrearCuentaRequest req, Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        String tipo = req.getTipo().trim().toUpperCase(Locale.ROOT);
        String titularidad = req.getTitularidad() == null
                ? "INDIVIDUAL"
                : req.getTitularidad().trim().toUpperCase(Locale.ROOT);
        if (!"INDIVIDUAL".equals(titularidad) && !"MANCOMUNADA".equals(titularidad)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de titularidad inválido");
        }

        Set<Long> titularesIds = req.getTitularesIds() == null
                ? new LinkedHashSet<>()
                : new LinkedHashSet<>(req.getTitularesIds());
        if (titularesIds.size() != (req.getTitularesIds() == null ? 0 : req.getTitularesIds().size())
                || titularesIds.contains(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La lista de titulares contiene duplicados");
        }
        if ("INDIVIDUAL".equals(titularidad)
                && (!titularesIds.isEmpty() || req.isRequiereAprobacion())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Las opciones de titulares y aprobación son solo para cuentas mancomunadas");
        }
        if ("MANCOMUNADA".equals(titularidad) && titularesIds.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Una cuenta mancomunada necesita al menos otro titular");
        }

        List<Usuario> titularesAdicionales = usuarioRepository.findAllById(titularesIds);
        if (titularesAdicionales.size() != titularesIds.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Uno o más titulares no existen");
        }

        FabricaCuenta fabrica = proveedorFabricaCuenta.obtener(tipo);
        String numero = generarNumeroCuentaUnico();
        Cuenta cuentaDominio = fabrica.abrirCuenta(numero, req.getSaldoInicial());
        CuentaEntity entity = new CuentaEntity();
        entity.setNumero(cuentaDominio.getNumero());
        entity.setSaldo(cuentaDominio.getSaldo());
        entity.setTipo(tipo);
        entity.setTitularidad(titularidad);
        entity.setRequiereAprobacion("MANCOMUNADA".equals(titularidad) && req.isRequiereAprobacion());
        entity.getTitulares().add(usuario);
        entity.getTitulares().addAll(titularesAdicionales);
        return convertir(cuentaRepository.save(entity));
    }

    private String generarNumeroCuentaUnico() {
        String numero;
        do {
            String digitos = String.format(Locale.ROOT, "%016d", random.nextLong(RANGO_NUMERO_CUENTA));
            numero = CODIGO_BANCO + "-" + digitos;
        } while (cuentaRepository.existsByNumero(numero));
        return numero;
    }

    @Transactional(readOnly = true)
    public List<CuentaResponse> listarCuentas(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado");
        }
        return cuentaRepository.findByTitularId(usuarioId).stream()
                .map(this::convertir)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CuentaResponse obtenerPorNumero(String numero, Long usuarioId) {
        CuentaEntity c = cuentaRepository.findByNumeroAndTitularId(numero, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        return convertir(c);
    }

    private CuentaResponse convertir(CuentaEntity cuenta) {
        List<TitularResponse> titulares = cuenta.getTitulares().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .map(titular -> new TitularResponse(titular.getId(), titular.getNombre()))
                .toList();
        return new CuentaResponse(cuenta.getNumero(), cuenta.getTipo(), cuenta.getSaldo(),
                cuenta.getTitularidad(), cuenta.isRequiereAprobacion(), titulares);
    }
}