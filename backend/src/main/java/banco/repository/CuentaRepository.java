package banco.repository;

import banco.model.CuentaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CuentaRepository extends JpaRepository<CuentaEntity, Long> {
    Optional<CuentaEntity> findByNumero(String numero);
    @Query("select distinct c from CuentaEntity c join c.titulares t where c.numero = :numero and t.id = :usuarioId")
    Optional<CuentaEntity> findByNumeroAndTitularId(@Param("numero") String numero,
            @Param("usuarioId") Long usuarioId);

    @Query("select distinct c from CuentaEntity c join c.titulares t where t.id = :usuarioId")
    List<CuentaEntity> findByTitularId(@Param("usuarioId") Long usuarioId);

    boolean existsByNumero(String numero);
}