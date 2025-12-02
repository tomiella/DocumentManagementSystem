package at.bif.swen.ocrworker.service;

import at.bif.swen.ocrworker.config.MinioProperties;
import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private MinioProperties properties;

    @Mock
    private GetObjectResponse getObjectResponse;

    @InjectMocks
    private MinioService minioService;

    private static final String TEST_BUCKET = "test-bucket";
    private static final String TEST_FILENAME = "test-file.pdf";

    @BeforeEach
    void setUp() {
        when(properties.getBucket()).thenReturn(TEST_BUCKET);
    }

    @Test
    void getFile_whenBucketExists_shouldReturnInputStream() throws Exception {
        // Arrange
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        // Act
        InputStream result = minioService.getFile(TEST_FILENAME);

        // Assert
        assertNotNull(result);
        assertEquals(getObjectResponse, result);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).getObject(any(GetObjectArgs.class));
        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void getFile_whenBucketDoesNotExist_shouldCreateBucketAndReturnInputStream() throws Exception {
        // Arrange
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);
        when(minioClient.getObject(any(GetObjectArgs.class))).thenReturn(getObjectResponse);

        // Act
        InputStream result = minioService.getFile(TEST_FILENAME);

        // Assert
        assertNotNull(result);
        assertEquals(getObjectResponse, result);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void getFile_whenBucketExistsCheckFails_shouldThrowRuntimeException() throws Exception {
        // Arrange
        when(minioClient.bucketExists(any(BucketExistsArgs.class)))
                .thenThrow(new RuntimeException("MinIO connection failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            minioService.getFile(TEST_FILENAME);
        });

        // The exception is wrapped, so check for either message
        String message = exception.getMessage();
        assertTrue(message.contains("Failed to ensure bucket exists") ||
                message.contains("Failed to fetch file from MinIO"),
                "Expected exception message to contain error info, but was: " + message);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
    }

    @Test
    void getFile_whenGetObjectFails_shouldThrowRuntimeException() throws Exception {
        // Arrange
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);
        when(minioClient.getObject(any(GetObjectArgs.class)))
                .thenThrow(new RuntimeException("File not found"));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            minioService.getFile(TEST_FILENAME);
        });

        assertTrue(exception.getMessage().contains("Failed to fetch file from MinIO"));
        assertTrue(exception.getMessage().contains(TEST_FILENAME));
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).getObject(any(GetObjectArgs.class));
    }

    @Test
    void getFile_whenMakeBucketFails_shouldThrowRuntimeException() throws Exception {
        // Arrange
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);
        doThrow(new RuntimeException("Permission denied"))
                .when(minioClient).makeBucket(any(MakeBucketArgs.class));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            minioService.getFile(TEST_FILENAME);
        });

        // The exception is wrapped, so check for either message
        String message = exception.getMessage();
        assertTrue(message.contains("Failed to ensure bucket exists") ||
                message.contains("Failed to fetch file from MinIO"),
                "Expected exception message to contain error info, but was: " + message);
        verify(minioClient).bucketExists(any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }
}
