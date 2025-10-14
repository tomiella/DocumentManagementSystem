package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.dto.DocumentUpdateRequest;
import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;

    @PostMapping
    public ResponseEntity<DocumentDto> create(@Valid @RequestBody DocumentCreateRequest req) {
        Document saved = documentService.create(req);
        return ResponseEntity.created(URI.create("/documents/" + saved.getId()))
                .body(DocumentMapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable UUID id) {
        return DocumentMapper.toDto(documentService.get(id));
    }

    @GetMapping
    public List<DocumentDto> list(@RequestParam(required = false) String title) {
        return documentService.search(title).stream().map(DocumentMapper::toDto).toList();
    }

    @PatchMapping("/{id}")
    public DocumentDto update(@PathVariable UUID id, @Valid @RequestBody DocumentUpdateRequest req) {
        return DocumentMapper.toDto(documentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
