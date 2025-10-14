package at.bif.swen.rest.service;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.exception.NotFoundException;
import at.bif.swen.rest.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    @Transactional
    public Document create(DocumentCreateRequest req) {
        Document d = Document.builder()
                .title(req.title())
                .filename(req.filename())
                .contentType(req.contentType())
                .size(req.size())
                .summary(req.summary())
                .build();
        return documentRepository.save(d);
    }

    @Transactional(readOnly = true)
    public Document get(UUID id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Document> search(String title) {
        if (title != null && !title.isBlank()) {
            return documentRepository.findByTitleContainingIgnoreCase(title.trim());
        }
        return documentRepository.findAll();
    }

    @Transactional
    public Document update(UUID id, DocumentUpdateRequest req) {
        Document d = get(id);
        if (req.title() != null) d.setTitle(req.title());
        if (req.summary() != null) d.setSummary(req.summary());
        return documentRepository.save(d);
    }

    @Transactional
    public void delete(UUID id) {
        Document d = get(id);
        documentRepository.delete(d);
    }
}
