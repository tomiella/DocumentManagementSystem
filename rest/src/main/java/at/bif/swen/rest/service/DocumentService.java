package at.bif.swen.rest.service;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.dto.DocumentDto;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;

import at.bif.swen.rest.exception.NotFoundException;
import at.bif.swen.rest.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {


    private final DocumentRepository docRepo;
    private final StoragePort storage;
    private final DocumentMapper mapper;

    @Transactional
    public Document create(DocumentCreateRequest req) {

        //Todo:

        Document d = Document.builder()
                .title(req.title())
                .filename(req.filename())
                .contentType(req.contentType())
                .size(req.size())
                .summary(req.summary())
                .build();

        return docRepo.save(d);
    }


    @Transactional(readOnly = true)
    public Document get(UUID id) {

        return docRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }


    @Transactional(readOnly = true)
    public List<Document> search(String title) {

        if (title != null && !title.isBlank()) {
            return docRepo.findByTitleContainingIgnoreCase(title.trim());
        }

        return docRepo.findAll();
    }

    @Transactional
    public Document update(UUID id, DocumentUpdateRequest req) {

        Document d = get(id);
        if (req.title() != null) d.setTitle(req.title());
        if (req.summary() != null) d.setSummary(req.summary());

        return docRepo.save(d);
    }

    @Transactional
    public void delete(UUID id){
        Document doc = get(id);

        if(doc.getFilename() != null){
            storage.delete(doc.getFilename());
        }

        docRepo.delete(doc);
    }

    @Transactional
    public DocumentDto createFromUpload(String title, String summary, MultipartFile file) throws Exception {

        String key = storage.store(file);

        Document doc = new Document();
        doc.setTitle(title);
        doc.setSummary((summary==null|| summary.isBlank()) ? null : summary.trim());
        doc.setContentType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setUploadedAt(OffsetDateTime.now());
        doc.setFilename(key);

        doc = docRepo.save(doc);

        return mapper.toDto(doc);
    }

    @Transactional(readOnly = true)
    public Resource loadFile(UUID id) throws IOException {
        Document doc = get(id); // reuse existing get()
        return storage.loadAsResource(doc.getFilename());
    }
}
