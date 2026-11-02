package banco.repository;

import banco.model.MovimientoEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimientoRepository extends JpaRepository<MovimientoEntity, Long> {
    List<MovimientoEntity> findByCuentaIdOrderByFechaDesc(Long cuentaId);
}