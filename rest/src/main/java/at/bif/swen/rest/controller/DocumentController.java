package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.dto.DocumentUpdateRequest;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.AccessTrackingService;
import at.bif.swen.rest.service.DocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final DocumentMapper mapper;
    private final AccessTrackingService accessTrackingService;

    @PostMapping
    public ResponseEntity<DocumentDto> create(@Valid @RequestBody DocumentCreateRequest req) {
        Document saved = documentService.create(req);
        return ResponseEntity
                .created(java.util.Objects.requireNonNull(URI.create("/documents/" + saved.getId())))
                .body(mapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable UUID id) {
        accessTrackingService.recordAccess(id);
        return mapper
                .toDto(documentService.get(id));
    }

    @GetMapping
    public List<DocumentDto> list(@RequestParam(required = false) String title) {
        List<DocumentDto> documents = documentService
                .search(title)
                .stream()
                .map(mapper::toDto)
                .toList();
        documents.forEach(doc -> accessTrackingService.recordAccess(doc.id()));
        return documents;
    }

    // Note: UPLOAD

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "summary", required = false) String summary) {
        try {
            // Create a new Document entity in the database
            // The service handles storage via StoragePort (which is now
            // MinioStorageService)
            Document savedDocument = documentService.createFromUpload(title, summary, file);

            // Return the created document details
            return ResponseEntity
                    .created(URI.create("/documents/" + savedDocument.getId()))
                    .body(mapper.toDto(savedDocument));

        } catch (Exception e) {
            return ResponseEntity
                    .status(500)
                    .body("Error uploading file: " + e.getMessage());
        }
    }
    /*
     * @PostMapping(path = "/upload", consumes =
     * MediaType.MULTIPART_FORM_DATA_VALUE)
     * public ResponseEntity<DocumentDto> upload(
     * 
     * @RequestPart("file") MultipartFile file,
     * 
     * @RequestPart("title") String title,
     * 
     * @RequestPart(value = "summary", required = false) String summary
     * ) throws Exception {
     * 
     * DocumentDto dto = documentService.createFromUpload(title, summary, file);
     * 
     * return ResponseEntity
     * .created(URI.create("/documents/" + dto.id()))
     * .body(dto);
     * }
     */

    // Note: Download
    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> download(@PathVariable UUID id) throws Exception {
        accessTrackingService.recordAccess(id);
        var doc = documentService.get(id);

        Resource resource = documentService.loadFile(id);

        String contentType = doc.getContentType() != null
                ? doc.getContentType()
                : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + doc.getFilename() + "\"")
                .body(resource);
    }

    // Note: update
    @PatchMapping("/{id}")
    public DocumentDto update(@PathVariable UUID id, @Valid @RequestBody DocumentUpdateRequest req) {
        return mapper.toDto(documentService.update(id, req));
    }

    // Note: Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
