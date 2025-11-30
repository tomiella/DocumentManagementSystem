package at.bif.swen.rest.service;

import at.bif.swen.rest.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.errors.MinioException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioStorageService(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    public void upload(String objectName, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {



            // Ensure bucket exists with Retry
            ensureBucketExistsWithRetry();

            // Upload the file
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(objectName)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());

        } catch (MinioException e) {
            throw new RuntimeException("Error uploading to MinIO", e);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error during MinIO upload", e);
        }
    }


// Question: move this to a separate class? ==> do it as an initial step in the application startup??
    private void ensureBucketExistsWithRetry() {
        int maxAttempts = 3;
        long backoffMillis = 1000; // 1 second initial backoff

        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                boolean found = minioClient.bucketExists(
                        BucketExistsArgs.builder()
                                .bucket(properties.getBucket())
                                .build()
                );

                if (!found) {
                    minioClient.makeBucket(
                            MakeBucketArgs.builder()
                                    .bucket(properties.getBucket())
                                    .build()
                    );
                }

                // success -> just return
                return;

            } catch (Exception e) {
                if (attempt == maxAttempts) {
                    // after 3 tries, give up
                    throw new RuntimeException(
                            "Failed to ensure MinIO bucket " + properties.getBucket()
                                    + " exists after " + maxAttempts + " attempts", e);
                }

                // optional logging
                // log.warn("Attempt {}/{} to ensure MinIO bucket failed, retrying...", attempt, maxAttempts, e);

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