package at.bif.swen.ocrworker.consumer;

import at.bif.swen.ocrworker.config.AmqpConfig;
import at.bif.swen.ocrworker.messaging.DocumentCreatedEvent;
import at.bif.swen.ocrworker.messaging.OcrResultEvent;
import at.bif.swen.ocrworker.service.MinioService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OcrConsumerTest {

    @Mock
    private MinioService minioService;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private OcrConsumer ocrConsumer;

    private static final UUID TEST_DOC_ID = UUID.randomUUID();
    private static final String TEST_FILENAME = "test-document.pdf";
    private static final String TEST_CONTENT_TYPE = "application/pdf";
    private static final long TEST_SIZE = 1024L;

    @Test
    void onDocument_whenMinioServiceFails_shouldHandleExceptionGracefully() {
        // Arrange
        DocumentCreatedEvent event = new DocumentCreatedEvent(
                TEST_DOC_ID,
                TEST_FILENAME,
                TEST_CONTENT_TYPE,
                TEST_SIZE);

        when(minioService.getFile(TEST_FILENAME))
                .thenThrow(new RuntimeException("MinIO connection failed"));

        // Act - should not throw exception
        assertDoesNotThrow(() -> ocrConsumer.onDocument(event));

        // Assert
        verify(minioService).getFile(TEST_FILENAME);
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(),
                anyString(),
                any(Object.class));
    }

    @Test
    void onDocument_whenOcrProcessingFails_shouldHandleExceptionGracefully() {
        // Arrange
        DocumentCreatedEvent event = new DocumentCreatedEvent(
                TEST_DOC_ID,
                "invalid-file.xyz",
                "application/octet-stream",
                TEST_SIZE);

        // Return invalid data that will cause OCR to fail
        byte[] invalidData = new byte[] { 0x00, 0x01, 0x02 };
        InputStream invalidStream = new ByteArrayInputStream(invalidData);

        when(minioService.getFile("invalid-file.xyz")).thenReturn(invalidStream);

        // Act - should not throw exception
        assertDoesNotThrow(() -> ocrConsumer.onDocument(event));

        // Assert
        verify(minioService).getFile("invalid-file.xyz");
        // Should not publish result if OCR fails
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(),
                anyString(),
                any(Object.class));
    }

    @Test
    void onDocument_whenEventHasNullFilename_shouldHandleGracefully() {
        // Arrange
        DocumentCreatedEvent event = new DocumentCreatedEvent(
                TEST_DOC_ID,
                null,
                TEST_CONTENT_TYPE,
                TEST_SIZE);

        // Act - should not throw exception
        assertDoesNotThrow(() -> ocrConsumer.onDocument(event));

        // Assert - should not attempt to fetch file with null filename
        verify(rabbitTemplate, never()).convertAndSend(
                anyString(),
                anyString(),
                any(Object.class));
    }

    @Test
    void onDocument_shouldNotThrowExceptionOnAnyError() {
        // Arrange
        DocumentCreatedEvent event = new DocumentCreatedEvent(
                TEST_DOC_ID,
                TEST_FILENAME,
                TEST_CONTENT_TYPE,
                TEST_SIZE);

        // Simulate any unexpected error
        when(minioService.getFile(TEST_FILENAME))
                .thenThrow(new NullPointerException("Unexpected error"));

        // Act & Assert - should handle all exceptions gracefully
        assertDoesNotThrow(() -> ocrConsumer.onDocument(event));

        verify(minioService).getFile(TEST_FILENAME);
    }
}
