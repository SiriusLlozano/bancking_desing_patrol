package banco.service;

import banco.domain.cuenta.Cuenta;
import banco.domain.cuenta.FabricaCuenta;
import banco.dto.MovimientoRequest;
import banco.dto.MovimientoResponse;
import banco.dto.TransferenciaRequest;
import banco.dto.TransferenciaResponse;
import banco.model.CuentaEntity;
import banco.model.MovimientoEntity;
import banco.repository.CuentaRepository;
import banco.repository.MovimientoRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class MovimientoService {
    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ProveedorFabricaCuenta proveedorFabricaCuenta;

    public MovimientoService(CuentaRepository cuentaRepository, MovimientoRepository movimientoRepository,
            ProveedorFabricaCuenta proveedorFabricaCuenta) {
        this.cuentaRepository = cuentaRepository;
        this.movimientoRepository = movimientoRepository;
        this.proveedorFabricaCuenta = proveedorFabricaCuenta;
    }

    @Transactional
    public MovimientoResponse registrar(String numeroCuenta, Long usuarioId, MovimientoRequest req) {
        CuentaEntity cuentaEntity = cuentaRepository.findByNumeroAndTitularId(numeroCuenta, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        String tipo = req.getTipo().trim().toUpperCase(Locale.ROOT);
        FabricaCuenta fabrica = proveedorFabricaCuenta.obtener(cuentaEntity.getTipo());
        Cuenta cuenta = fabrica.crearCuenta(cuentaEntity.getNumero(), cuentaEntity.getSaldo());

        if ("DEPOSITO".equals(tipo)) {
            cuenta.depositar(req.getMonto());
        } else if ("RETIRO".equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Genera un código de retiro y valídalo en el cajero");
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de movimiento inválido");
        }

        cuentaEntity.setSaldo(cuenta.getSaldo());
        cuentaRepository.save(cuentaEntity);

        MovimientoEntity movimiento = new MovimientoEntity();
        movimiento.setCuenta(cuentaEntity);
        movimiento.setTipo(tipo);
        movimiento.setMonto(req.getMonto());
        movimiento.setSaldoResultante(cuenta.getSaldo());
        movimiento.setFecha(LocalDateTime.now());
        return convertir(movimientoRepository.save(movimiento));
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar(String numeroCuenta, Long usuarioId) {
        CuentaEntity cuenta = cuentaRepository.findByNumeroAndTitularId(numeroCuenta, usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta no encontrada"));
        return movimientoRepository.findByCuentaIdOrderByFechaDesc(cuenta.getId()).stream()
                .map(this::convertir)
                .toList();
    }

    @Transactional
    public TransferenciaResponse transferir(TransferenciaRequest req, Long usuarioId) {
        if (req.getNumeroOrigen().equals(req.getNumeroDestino())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Elige una cuenta de destino distinta");
        }
        CuentaEntity origen = cuentaRepository.findByNumeroAndTitularId(req.getNumeroOrigen(), usuarioId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta de origen no encontrada"));
        CuentaEntity destino = cuentaRepository.findByNumero(req.getNumeroDestino())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cuenta de destino no encontrada"));

        FabricaCuenta fabricaOrigen = proveedorFabricaCuenta.obtener(origen.getTipo());
        FabricaCuenta fabricaDestino = proveedorFabricaCuenta.obtener(destino.getTipo());
        Cuenta cuentaOrigen = fabricaOrigen.crearCuenta(origen.getNumero(), origen.getSaldo());
        Cuenta cuentaDestino = fabricaDestino.crearCuenta(destino.getNumero(), destino.getSaldo());
        if (!cuentaOrigen.retirar(req.getMonto())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para la transferencia");
        }
        cuentaDestino.depositar(req.getMonto());

        origen.setSaldo(cuentaOrigen.getSaldo());
        destino.setSaldo(cuentaDestino.getSaldo());
        cuentaRepository.save(origen);
        cuentaRepository.save(destino);
        guardarMovimiento(origen, "TRANSFERENCIA_ENVIADA", req.getMonto(), cuentaOrigen.getSaldo());
        guardarMovimiento(destino, "TRANSFERENCIA_RECIBIDA", req.getMonto(), cuentaDestino.getSaldo());
        return new TransferenciaResponse(origen.getNumero(), destino.getNumero(), req.getMonto(),
                cuentaOrigen.getSaldo(), cuentaDestino.getSaldo());
    }

    private void guardarMovimiento(CuentaEntity cuenta, String tipo, double monto, double saldoResultante) {
        MovimientoEntity movimiento = new MovimientoEntity();
        movimiento.setCuenta(cuenta);
        movimiento.setTipo(tipo);
        movimiento.setMonto(monto);
        movimiento.setSaldoResultante(saldoResultante);
        movimiento.setFecha(LocalDateTime.now());
        movimientoRepository.save(movimiento);
    }

    private MovimientoResponse convertir(MovimientoEntity movimiento) {
        return new MovimientoResponse(movimiento.getId(), movimiento.getCuenta().getNumero(),
                movimiento.getTipo(), movimiento.getMonto(), movimiento.getSaldoResultante(), movimiento.getFecha());
    }
}