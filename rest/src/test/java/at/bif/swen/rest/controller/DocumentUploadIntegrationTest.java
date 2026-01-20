package at.bif.swen.rest.controller;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.repository.DocumentRepository;
import at.bif.swen.rest.service.SearchService;
import at.bif.swen.rest.service.StoragePort;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser(username = "admin", roles = { "USER" })
class DocumentUploadIntegrationTest {

        @Autowired
        private MockMvc mvc;

        @Autowired
        private DocumentRepository documentRepository;

        @MockBean
        private StoragePort storagePort;

        @MockBean
        private SearchService searchService;

        @MockBean
        private RabbitTemplate rabbitTemplate;

        @Test
        void shouldUploadDocumentAndPersist() throws Exception {
                // Arrange
                String title = "Integration Test Doc";
                String summary = "This is a summary of the uploaded document.";
                String filename = "test-doc.pdf";
                String contentType = "application/pdf";
                byte[] content = "Dummy PDF Content".getBytes();

                MockMultipartFile file = new MockMultipartFile(
                                "file",
                                filename,
                                contentType,
                                content);

                // Mock StoragePort to return a key (e.g., the original filename or a UUID)
                when(storagePort.store(any())).thenReturn("stored-" + filename);

                // Act
                mvc.perform(multipart("/documents/upload")
                                .file(file)
                                .param("title", title)
                                .param("summary", summary))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.title").value(title))
                                .andExpect(jsonPath("$.filename").value("stored-" + filename))
                                .andExpect(jsonPath("$.summary").value(summary));

                // Assert - Database Persistence
                Document savedDoc = documentRepository.findAll().stream()
                                .filter(d -> d.getTitle().equals(title))
                                .findFirst()
                                .orElse(null);

                assertThat(savedDoc).isNotNull();
                assertThat(savedDoc.getFilename()).isEqualTo("stored-" + filename);
                assertThat(savedDoc.getContentType()).isEqualTo(contentType);
                assertThat(savedDoc.getSize()).isEqualTo(content.length);
                assertThat(savedDoc.getUploadedAt()).isNotNull();

                // Assert - Interaction with Dependencies
                verify(storagePort).store(any());
                verify(searchService).indexUploadedFile(any(Document.class), any());
                verify(rabbitTemplate).convertAndSend(anyString(), anyString(), any(Object.class));
        }
}
