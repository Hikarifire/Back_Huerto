package Duoc.Huerto.service;

import Duoc.Huerto.exception.RecursoNoEncontradoException;
import Duoc.Huerto.exception.ReglaNegocioException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HexFormat;
import java.util.UUID;

@Service
public class ArchivoStorageService {
    private final Path carpeta;

    public ArchivoStorageService(@Value("${app.upload-dir:uploads}") String directorio) {
        carpeta = Path.of(directorio, "evidencias").toAbsolutePath().normalize();
    }

    public String guardarImagen(MultipartFile archivo) {
        if (archivo == null || archivo.isEmpty()) throw new ReglaNegocioException("Debes adjuntar una fotografía.");
        if (archivo.getSize() > 5 * 1024 * 1024) throw new ReglaNegocioException("La fotografía no puede superar 5 MB.");
        try (var contenido = archivo.getInputStream()) {
            byte[] inicio = contenido.readNBytes(12);
            String hex = HexFormat.of().formatHex(inicio);
            String extension;
            if (hex.startsWith("ffd8ff")) extension = ".jpg";
            else if (hex.startsWith("89504e470d0a1a0a")) extension = ".png";
            else if (hex.startsWith("52494646") && hex.endsWith("57454250")) extension = ".webp";
            else throw new ReglaNegocioException("Formato de imagen no permitido. Usa JPG, PNG o WEBP.");

            Files.createDirectories(carpeta);
            String nombre = UUID.randomUUID() + extension;
            Path destino = carpeta.resolve(nombre);
            try (var salida = Files.newOutputStream(destino)) {
                salida.write(inicio);
                contenido.transferTo(salida);
            } catch (IOException ex) {
                Files.deleteIfExists(destino);
                throw ex;
            }

            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                    @Override
                    public void afterCompletion(int status) {
                        if (status == STATUS_ROLLED_BACK) {
                            try {
                                Files.deleteIfExists(destino);
                            } catch (IOException ex) {
                                System.getLogger(ArchivoStorageService.class.getName())
                                    .log(System.Logger.Level.ERROR, "No se pudo limpiar una carga revertida", ex);
                            }
                        }
                    }
                });
            }
            return nombre;
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo guardar la fotografía.", ex);
        }
    }

    public FileSystemResource abrirImagen(String referencia) {
        String nombre = referencia.substring(referencia.lastIndexOf('/') + 1);
        if (!nombre.matches("[a-f0-9-]{36}\\.(jpg|png|webp)")) {
            throw new RecursoNoEncontradoException("Fotografía no encontrada.");
        }
        Path ruta = carpeta.resolve(nombre);
        if (!Files.isRegularFile(ruta)) throw new RecursoNoEncontradoException("Fotografía no encontrada.");
        return new FileSystemResource(ruta);
    }
}
