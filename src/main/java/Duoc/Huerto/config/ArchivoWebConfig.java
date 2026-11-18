package Duoc.Huerto.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;

@Configuration
public class ArchivoWebConfig implements WebMvcConfigurer {
    private final String uploadDir;
    public ArchivoWebConfig(@Value("${app.upload-dir:uploads}") String uploadDir) { this.uploadDir = uploadDir; }
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String location = Path.of(uploadDir, "evidencias").toAbsolutePath().normalize().toUri().toString();
        if (!location.endsWith("/")) location += "/";
        registry.addResourceHandler("/uploads/evidencias/**").addResourceLocations(location);
    }
}
