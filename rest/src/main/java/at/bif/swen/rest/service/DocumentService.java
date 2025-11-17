package at.bif.swen.rest.service;

import at.bif.swen.rest.config.AmqpConfig;
import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.exception.NotFoundException;
import at.bif.swen.rest.messaging.DocumentCreatedEvent;
import at.bif.swen.rest.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final RabbitTemplate rabbit;
    private static final Logger log = LoggerFactory.getLogger(DocumentService.class);

    @Transactional
    public Document create(DocumentCreateRequest req) {
        Document d = Document.builder()
                .title(req.title())
                .filename(req.filename())
                .contentType(req.contentType())
                .size(req.size())
                .summary(req.summary())
                .build();
        Document saved =  documentRepository.save(d);

        DocumentCreatedEvent evt = new DocumentCreatedEvent(saved.getId(), saved.getFilename(), saved.getContentType(), saved.getSize());
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
