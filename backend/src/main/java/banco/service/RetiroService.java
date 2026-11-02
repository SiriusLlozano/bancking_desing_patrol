package banco.service;

import banco.domain.cuenta.Cuenta;
import banco.domain.cuenta.AutorizacionInmediata;
import banco.domain.cuenta.AutorizacionTodosLosTitulares;
import banco.dto.CrearRetiroRequest;
import banco.dto.RetiroResponse;
import banco.dto.ValidarRetiroResponse;
import banco.model.CuentaEntity;
import banco.model.MovimientoEntity;
import banco.model.RetiroEntity;
import banco.model.Usuario;
import banco.repository.CuentaRepository;
import banco.repository.MovimientoRepository;
import banco.repository.RetiroRepository;
import banco.repository.UsuarioRepository;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RetiroService {
    private static final String PENDIENTE = "PENDIENTE";
    private static final String AUTORIZADO = "AUTORIZADO";
    private static final String EJECUTADO = "EJECUTADO";

    private final CuentaRepository cuentaRepository;
    private final RetiroRepository retiroRepository;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProveedorFabricaCuenta proveedorFabricaCuenta;
    private final SecureRandom random = new SecureRandom();

    public RetiroService(CuentaRepository cuentaRepository, RetiroRepository retiroRepository,
            MovimientoRepository movimientoRepository, UsuarioRepository usuarioRepository,
            ProveedorFabricaCuenta proveedorFabricaCuenta) {
        this.cuentaRepository = cuentaRepository;
        this.retiroRepository = retiroRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
        this.proveedorFabricaCuenta = proveedorFabricaCuenta;
    }

    @Transactional
    public RetiroResponse generarCodigo(String numeroCuenta, Long usuarioId, CrearRetiroRequest req) {
        CuentaEntity cuenta = cuentaRepository.findByNumeroAndTitularId(numeroCuenta, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        Cuenta cuentaDominio = crearCuentaDominio(cuenta);
        if (!cuentaDominio.retirar(req.getMonto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente o retiro no permitido");
        }

        Usuario solicitante = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cliente no encontrado"));
        RetiroEntity retiro = new RetiroEntity();
        retiro.setCuenta(cuenta);
        retiro.setMonto(req.getMonto());
        retiro.setUsado(false);
        retiro.setSolicitante(solicitante);

        if (cuentaDominio.aprobacionesRequeridas(cuenta.getTitulares().size()) > 0) {
            retiro.setEstado(PENDIENTE);
        } else {
            autorizarConCodigo(retiro);
        }
        return convertir(retiroRepository.save(retiro), usuarioId);
    }

    @Transactional(readOnly = true)
    public List<RetiroResponse> listarSolicitudes(Long usuarioId) {
        return retiroRepository.findByTitularId(usuarioId).stream()
                .filter(retiro -> retiro.getSolicitante().getId().equals(usuarioId)
                        || PENDIENTE.equals(retiro.getEstado()))
                .map(retiro -> convertir(retiro, usuarioId))
                .toList();
    }

    @Transactional
    public RetiroResponse aprobar(Long retiroId, Long usuarioId) {
        RetiroEntity retiro = retiroRepository.findById(retiroId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        if (!PENDIENTE.equals(retiro.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La solicitud ya no está pendiente");
        }
        CuentaEntity cuenta = retiro.getCuenta();
        if (crearCuentaDominio(cuenta).aprobacionesRequeridas(cuenta.getTitulares().size()) == 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Esta cuenta no requiere aprobaciones");
        }
        if (retiro.getSolicitante().getId().equals(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Quien solicita el retiro no puede aprobarlo");
        }
        if (cuenta.getTitulares().stream().noneMatch(titular -> titular.getId().equals(usuarioId))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No eres titular de esta cuenta");
        }
        if (retiro.getAprobadores().stream().anyMatch(aprobador -> aprobador.getId().equals(usuarioId))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya aprobaste esta solicitud");
        }

        Usuario aprobador = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Titular no encontrado"));
        retiro.getAprobadores().add(aprobador);
        int aprobacionesRequeridas = cuenta.getTitulares().size() - 1;
        if (retiro.getAprobadores().size() == aprobacionesRequeridas) {
            Cuenta cuentaDominio = crearCuentaDominio(cuenta);
            if (!cuentaDominio.retirar(retiro.getMonto())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT, "El saldo ya no alcanza para completar este retiro");
            }
            autorizarConCodigo(retiro);
        }
        return convertir(retiroRepository.save(retiro), usuarioId);
    }

    @Transactional
    public ValidarRetiroResponse validarEnCajero(String codigoIngresado) {
        String codigo = codigoIngresado.trim();
        RetiroEntity retiro = retiroRepository.findByCodigo(codigo)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Código de retiro no válido"));
        if (retiro.isUsado()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este código ya fue utilizado");
        }
        if (!AUTORIZADO.equals(retiro.getEstado())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El retiro aún no está autorizado");
        }
        if (retiro.getVenceEn().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.GONE, "El código de retiro venció");
        }

        CuentaEntity cuenta = cuentaRepository.findById(retiro.getCuenta().getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        Cuenta cuentaDominio = crearCuentaDominio(cuenta);
        if (!cuentaDominio.retirar(retiro.getMonto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La cuenta ya no tiene saldo suficiente");
        }

        cuenta.setSaldo(cuentaDominio.getSaldo());
        cuentaRepository.save(cuenta);
        retiro.setUsado(true);
        retiro.setEstado(EJECUTADO);
        retiroRepository.save(retiro);

        MovimientoEntity movimiento = new MovimientoEntity();
        movimiento.setCuenta(cuenta);
        movimiento.setTipo("RETIRO");
        movimiento.setMonto(retiro.getMonto());
        movimiento.setSaldoResultante(cuenta.getSaldo());
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);

        return new ValidarRetiroResponse(cuenta.getNumero(), retiro.getMonto(), cuenta.getSaldo());
    }

    private void autorizarConCodigo(RetiroEntity retiro) {
        retiro.setCodigo(generarCodigoUnico());
        retiro.setVenceEn(LocalDateTime.now().plusMinutes(10));
        retiro.setEstado(AUTORIZADO);
    }

    private RetiroResponse convertir(RetiroEntity retiro, Long usuarioId) {
        boolean esSolicitante = retiro.getSolicitante().getId().equals(usuarioId);
        CuentaEntity cuenta = retiro.getCuenta();
        int requeridas = crearCuentaDominio(cuenta).aprobacionesRequeridas(cuenta.getTitulares().size());
        boolean puedeAprobar = PENDIENTE.equals(retiro.getEstado())
                && !esSolicitante
                && cuenta.getTitulares().stream().anyMatch(titular -> titular.getId().equals(usuarioId))
                && retiro.getAprobadores().stream().noneMatch(aprobador -> aprobador.getId().equals(usuarioId));
        return new RetiroResponse(
                retiro.getId(),
                esSolicitante ? retiro.getCodigo() : null,
                cuenta.getNumero(),
                retiro.getMonto(),
                retiro.getVenceEn(),
                retiro.getEstado(),
                retiro.getSolicitante().getNombre(),
                retiro.getAprobadores().size(),
                requeridas,
                puedeAprobar);
    }

    private Cuenta crearCuentaDominio(CuentaEntity cuentaEntity) {
        Cuenta cuenta = proveedorFabricaCuenta.obtener(cuentaEntity.getTipo())
                .crearCuenta(cuentaEntity.getNumero(), cuentaEntity.getSaldo());
        if ("MANCOMUNADA".equals(cuentaEntity.getTitularidad()) && cuentaEntity.isRequiereAprobacion()) {
            cuenta.configurarReglaAutorizacionRetiro(new AutorizacionTodosLosTitulares());
        } else {
            cuenta.configurarReglaAutorizacionRetiro(new AutorizacionInmediata());
        }
        return cuenta;
    }

    private String generarCodigoUnico() {
        String codigo;
        do {
            codigo = String.format(Locale.ROOT, "%08d", random.nextInt(100_000_000));
        } while (retiroRepository.existsByCodigo(codigo));
        return codigo;
    }
}
