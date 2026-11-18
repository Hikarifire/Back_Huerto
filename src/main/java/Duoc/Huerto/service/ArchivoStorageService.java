package Duoc.Huerto.service;

import Duoc.Huerto.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

@Service
public class ArchivoStorageService {
    private static final Set<String> TIPOS_PERMITIDOS = Set.of("image/jpeg", "image/png", "image/webp");
    private final Path carpeta;
    public ArchivoStorageService(@Value("${app.upload-dir:uploads}") String directorio) {
        this.carpeta = Path.of(directorio, "evidencias").toAbsolutePath().normalize();
    }
    public String guardarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) throw new ReglaNegocioException("Debes adjuntar una fotografía.");
        if (archivo.getSize() > 5 * 1024 * 1024) throw new ReglaNegocioException("La fotografía no puede superar los 5 MB.");
        String tipo = archivo.getContentType() == null ? "" : archivo.getContentType().toLowerCase(Locale.ROOT);
        if (!TIPOS_PERMITIDOS.contains(tipo)) throw new ReglaNegocioException("Formato no permitido. Utiliza JPG, PNG o WEBP.");
        String extension = switch (tipo) { case "image/png" -> ".png"; case "image/webp" -> ".webp"; default -> ".jpg"; };
        String nombre = UUID.randomUUID() + extension;
        try {
            Files.createDirectories(carpeta);
            Path destino = carpeta.resolve(nombre).normalize();
            if (!destino.startsWith(carpeta)) throw new ReglaNegocioException("Nombre de archivo no válido.");
            Files.copy(archivo.getInputStream(), destino, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/evidencias/" + nombre;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar la fotografía.", ex);
        }
    }
}
