package banco.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "cuentas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String numero;

    @Column(nullable = false)
    private String tipo; // AHORROS, CORRIENTE, EMPRESARIAL

    @Column(nullable = false)
    private double saldo;

    @Column(nullable = false)
    private String titularidad = "INDIVIDUAL";

    @Column(nullable = false)
    private boolean requiereAprobacion;

    @ManyToMany
    @JoinTable(name = "cuenta_titulares",
            joinColumns = @JoinColumn(name = "cuenta_id"),
            inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    private Set<Usuario> titulares = new LinkedHashSet<>();
}