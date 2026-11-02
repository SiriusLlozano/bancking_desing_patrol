package banco.repository;

import banco.model.RetiroEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface RetiroRepository extends JpaRepository<RetiroEntity, Long> {
    Optional<RetiroEntity> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);

    @Query("select distinct r from RetiroEntity r join r.cuenta c join c.titulares t " +
            "where t.id = :titularId order by r.id desc")
    List<RetiroEntity> findByTitularId(@Param("titularId") Long titularId);
}