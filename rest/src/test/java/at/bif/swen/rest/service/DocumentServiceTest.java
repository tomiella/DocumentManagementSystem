package at.bif.swen.rest.service;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.exception.NotFoundException;
import at.bif.swen.rest.repository.DocumentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

    @Mock
    DocumentRepository documentRepository;
    @Mock
    SearchService searchService;
    @InjectMocks
    DocumentService documentService;

    @Test
    void create_saves() {
        var req = new DocumentCreateRequest("T", "a.pdf", "application/pdf", 10L, "s");
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Document saved = documentService.create(req);

        verify(documentRepository).save(any(Document.class));
    }

    @Test
    void get_throws_when_missing() {
        UUID id = UUID.randomUUID();
        when(documentRepository.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> documentService.get(id))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void update_sets_title_summary() {
        UUID id = UUID.randomUUID();
        Document existing = Document.builder().id(id).title("old").filename("a.pdf").contentType("application/pdf")
                .size(1L).build();
        when(documentRepository.findById(id)).thenReturn(Optional.of(existing));
        when(documentRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var req = new DocumentUpdateRequest("new", "sum");
        Document updated = documentService.update(id, req);

        assertThat(updated.getTitle()).isEqualTo("new");
        assertThat(updated.getSummary()).isEqualTo("sum");
    }
}
