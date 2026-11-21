package Duoc.Huerto.config;

import Duoc.Huerto.entity.TipoCuenta;
import Duoc.Huerto.entity.Usuario;
import Duoc.Huerto.repository.UsuarioRepository;
import java.nio.charset.StandardCharsets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AdminInicial implements ApplicationRunner {

    private static final String CORREO = "admin@huerto.cl";
    private static final String CLAVE_DEMO = "AdminDemo123!";

    private final UsuarioRepository usuarios;
    private final PasswordEncoder encoder;
    private final String claveInicial;
    private final boolean datosDemo;

    public AdminInicial(
            UsuarioRepository usuarios,
            PasswordEncoder encoder,
            @Value("${HUERTO_ADMIN_PASSWORD:}") String claveInicial,
            @Value("${app.seed-demo-data:false}") boolean datosDemo
    ) {
        this.usuarios = usuarios;
        this.encoder = encoder;
        this.claveInicial = claveInicial;
        this.datosDemo = datosDemo;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (claveInicial.isEmpty() && !datosDemo) {
            return;
        }

        var existente = usuarios.findByCorreoIgnoreCase(CORREO);
        if (existente.isPresent()) {
            if (existente.get().getTipoCuenta() != TipoCuenta.ADMIN) {
                throw new IllegalStateException(
                        "El correo " + CORREO + " pertenece a otro rol."
                );
            }
            return;
        }

        String clave = claveInicial.isEmpty() ? CLAVE_DEMO : claveInicial;
        if (clave.isBlank() || clave.length() < 12 ||
                clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalStateException(
                    "HUERTO_ADMIN_PASSWORD requiere al menos 12 caracteres y hasta 72 bytes."
            );
        }

        usuarios.save(new Usuario(
                "Administración del huerto",
                CORREO,
                encoder.encode(clave),
                TipoCuenta.ADMIN
        ));
    }
}