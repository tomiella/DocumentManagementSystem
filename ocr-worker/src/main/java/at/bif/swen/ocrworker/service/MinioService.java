package at.bif.swen.ocrworker.service;

import at.bif.swen.ocrworker.config.MinioProperties;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public InputStream getFile(String filename) {
        try {
            // Ensure bucket exists before trying to fetch
            ensureBucketExists();

            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(properties.getBucket())
                            .object(filename)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch file from MinIO: " + filename, e);
        }
    }

    private void ensureBucketExists() {
        try {
            boolean found = minioClient.bucketExists(
                    io.minio.BucketExistsArgs.builder()
                            .bucket(properties.getBucket())
                            .build());

            if (!found) {
                minioClient.makeBucket(
                        io.minio.MakeBucketArgs.builder()
                                .bucket(properties.getBucket())
                                .build());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to ensure bucket exists: " + properties.getBucket(), e);
        }
    }
}
