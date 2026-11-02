package banco.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "codigos_retiro")
@Getter
@Setter
@NoArgsConstructor
public class RetiroEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, length = 8)
    private String codigo;

    @ManyToOne(optional = false)
    @JoinColumn(name = "cuenta_id", nullable = false)
    private CuentaEntity cuenta;

    @Column(nullable = false)
    private double monto;

    @Column
    private LocalDateTime venceEn;

    @Column(nullable = false)
    private boolean usado;

    @Column(nullable = false)
    private String estado = "PENDIENTE";

    @ManyToOne(optional = false)
    @JoinColumn(name = "solicitante_id", nullable = false)
    private Usuario solicitante;

    @ManyToMany
    @JoinTable(name = "retiro_aprobaciones",
            joinColumns = @JoinColumn(name = "retiro_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> aprobadores = new LinkedHashSet<>();
}