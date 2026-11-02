package banco.config;

import banco.model.Usuario;
import banco.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {
    @Bean
    public CommandLineRunner initData(UsuarioRepository usuarioRepository) {
        return args -> {
            if (!usuarioRepository.existsByUsername("admin")) {
                Usuario admin = new Usuario();
                admin.setUsername("admin");
                admin.setPassword("admin123");
                admin.setNombre("Administrador");
                admin.setRol("ADMIN");
                usuarioRepository.save(admin);
            }
            if (!usuarioRepository.existsByUsername("demo")) {
                Usuario demo = new Usuario();
                demo.setUsername("demo");
                demo.setPassword("demo123");
                demo.setNombre("Usuario Demo");
                demo.setRol("USER");
                usuarioRepository.save(demo);
            }
        };
    }
}