package at.bif.swen.rest.service;

import at.bif.swen.rest.config.MinioProperties;
import io.minio.*;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
@Primary
public class MinioStorageService implements StoragePort {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public String store(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            // Ensure bucket exists with Retry
            ensureBucketExistsWithRetry();

            String objectName = UUID.randomUUID().toString();
            String originalFilename = file.getOriginalFilename();
            if (originalFilename != null && originalFilename.contains(".")) {
                objectName += originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // Upload the file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

            return objectName;

        } catch (Exception e) {
            throw new RuntimeException("Error uploading to MinIO", e);
        }
    }

    @Override
    public Resource loadAsResource(String key) {
        try {
            InputStream stream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(key)
                            .build());

            if (stream == null) {
                throw new RuntimeException("MinIO returned null stream for key: " + key);
            }
            return new InputStreamResource(stream);
        } catch (Exception e) {
            throw new RuntimeException("Error loading from MinIO", e);
        }
    }

    @Override
    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(key)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Error deleting from MinIO", e);
        }
    }

    private void ensureBucketExistsWithRetry() {
        int maxAttempts = 3;
        long backoffMillis = 1000; // 1 second initial backoff

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                boolean found = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(properties.getBucket())
                                .build());

                if (!found) {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(properties.getBucket())
                                    .build());
                }

                // success -> just return
                return;

            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    // after 3 tries, give up
                    throw new RuntimeException(
                            "Failed to ensure MinIO bucket " + properties.getBucket()
                                    + " exists after " + maxAttempts + " attempts",
                            e);
                }

                try {
                    Thread.sleep(backoffMillis * attempt); // simple linear backoff
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted while waiting to retry MinIO bucket creation", ie);
                }
            }
        }
    }
}