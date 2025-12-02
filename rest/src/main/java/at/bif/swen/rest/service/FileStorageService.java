package at.bif.swen.rest.service;

import at.bif.swen.rest.config.StorageProperties;
import lombok.RequiredArgsConstructor;

import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// @Service - Disabled in favor of MinioStorageService
@RequiredArgsConstructor
public class FileStorageService implements StoragePort {

    private final StorageProperties props;

    @Override
    public String store(MultipartFile file) throws IOException {

        String original = StringUtils.cleanPath(
                file.getOriginalFilename() == null
                        ? "upload"
                        : file.getOriginalFilename());

        String ext = original.contains(".")
                ? original.substring(original.lastIndexOf("."))
                : "";

        String generated = UUID.randomUUID().toString() + ext;

        Path root = Path.of(props.getUploadDir()).toAbsolutePath().normalize();
        Files.createDirectories(root);

        Path target = root.resolve(generated).normalize();
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        return generated; // return only the key.
    }

    @Override
    public Resource loadAsResource(String key) throws IOException {

        Path uploadDirPath = Path.of(props.getUploadDir()).toAbsolutePath().normalize();
        Path file = uploadDirPath.resolve(key).normalize();

        if (!Files.exists(file)) {
            throw new IOException("File not found" + key);
        }

        return new UrlResource(file.toUri());
    }

    @Override
    public void delete(String key) {
        if (key == null || key.isBlank()) {
            return;
        }

        try {
            Path root = Path.of(props.getUploadDir()).toAbsolutePath().normalize();
            Path file = root.resolve(key).normalize();

            if (Files.exists(file)) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete stored file: " + key, e);
        }
    }
}