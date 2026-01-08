package at.bif.swen.rest.service;

import at.bif.swen.rest.config.AmqpConfig;
import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.dto.DocumentDto;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;

import at.bif.swen.rest.exception.NotFoundException;
import at.bif.swen.rest.messaging.DocumentCreatedEvent;
import at.bif.swen.rest.repository.DocumentRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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

    private final StoragePort storage;
    private final DocumentMapper mapper;
    private final DocumentRepository documentRepository;
    private final RabbitTemplate rabbit;
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);
    private final SearchService searchService;

    @Transactional
    public Document create(DocumentCreateRequest req) {

        // Todo:

        Document d = Document.builder()
                .title(req.title())
                .filename(req.filename())
                .contentType(req.contentType())
                .size(req.size())
                .summary(req.summary())
                .build();

        Document saved = documentRepository.save(d);

        DocumentCreatedEvent evt = new DocumentCreatedEvent(saved.getId(), saved.getFilename(), saved.getContentType(),
                saved.getSize());
        try {
            rabbit.setMessageConverter(new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter());
            rabbit.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTING_KEY, evt);
            log.info("Published DocumentCreatedEvent id={} filename={}", saved.getId(), saved.getFilename());
        } catch (Exception e) {
            log.error("Failed to publish DocumentCreatedEvent id={} reason={}", saved.getId(), e.toString(), e);
            // TODO: maybe rollbakc???
        }

        return saved;
    }

    @Transactional(readOnly = true)
    public Document get(UUID id) {

        return documentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Document not found: " + id));
    }

    @Transactional(readOnly = true)
    public List<Document> search(String title) {
        /*
         * if (title != null && !title.isBlank()) {
         * return documentRepository.findByTitleContainingIgnoreCase(title.trim());
         * }
         * 
         * return documentRepository.findAll();
         */

        if (title == null || title.isBlank()) {
            return documentRepository.findAll();
        }

        String query = title.trim();

        List<UUID> ids = searchService.searchIds(query);
        if (ids.isEmpty()) {
            // fallback if ES not ready or no index yet
            return documentRepository.findByTitleContainingIgnoreCase(query);
        }

        List<Document> docs = documentRepository.findAllById(ids);

        Map<UUID, Document> mapById = new HashMap<>();
        for (Document d : docs)
            mapById.put(d.getId(), d);

        List<Document> ordered = new ArrayList<>();
        for (UUID id : ids) {
            Document d = mapById.get(id);
            if (d != null)
                ordered.add(d);
        }

        return ordered;

    }

    @Transactional
    public Document update(UUID id, DocumentUpdateRequest req) {

        Document d = get(id);
        if (req.title() != null)
            d.setTitle(req.title());

        if (req.summary() != null)
            d.setSummary(req.summary());

        Document saved = documentRepository.save(d);
        searchService.updateTitleSummary(saved);

        return saved;
    }

    @Transactional
    public void delete(UUID id) {
        Document doc = get(id);

        if (doc.getFilename() != null) {
            storage.delete(doc.getFilename());
        }

        documentRepository.delete(doc);
        searchService.deleteById(id);
    }

    @Transactional
    public Document createFromUpload(String title, String summary, MultipartFile file) throws Exception {

        String key = storage.store(file);
        log.info("Stored file {} with key {}", file.getOriginalFilename(), key);

        Document doc = new Document();
        doc.setTitle(title);
        doc.setSummary((summary == null || summary.isBlank()) ? null : summary.trim());
        doc.setContentType(file.getContentType());
        doc.setSize(file.getSize());
        doc.setUploadedAt(OffsetDateTime.now());
        doc.setFilename(key);

        doc = documentRepository.save(doc);
        searchService.indexUploadedFile(doc, file);

        // Publish event
        DocumentCreatedEvent evt = new DocumentCreatedEvent(doc.getId(), doc.getFilename(), doc.getContentType(),
                doc.getSize());
        try {
            rabbit.setMessageConverter(new org.springframework.amqp.support.converter.Jackson2JsonMessageConverter());
            rabbit.convertAndSend(AmqpConfig.EXCHANGE, AmqpConfig.ROUTING_KEY, evt);
            log.info("Published DocumentCreatedEvent id={} filename={}", doc.getId(), doc.getFilename());
        } catch (Exception e) {
            log.error("Failed to publish DocumentCreatedEvent id={} reason={}", doc.getId(), e.toString(), e);
        }

        return doc;
    }

    @Transactional(readOnly = true)
    public Resource loadFile(UUID id) throws IOException {
        Document doc = get(id); // reuse existing get()
        return storage.loadAsResource(doc.getFilename());
    }
}
