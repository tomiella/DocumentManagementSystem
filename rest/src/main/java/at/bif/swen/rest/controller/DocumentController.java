package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.dto.DocumentUpdateRequest;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.DocumentService;
import at.bif.swen.rest.service.MinioStorageService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.io.IOException;

@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final MinioStorageService minioStorageService;
    private final DocumentMapper mapper;

    @PostMapping
    public ResponseEntity<DocumentDto> create(@Valid @RequestBody DocumentCreateRequest req) {
        Document saved = documentService.create(req);
        return ResponseEntity
                .created(URI.create("/documents/" + saved.getId()))
                .body(mapper.toDto(saved));
    }

    @GetMapping("/{id}")
    public DocumentDto get(@PathVariable UUID id) {
        return mapper
                .toDto(documentService.get(id));
    }

    @GetMapping
    public List<DocumentDto> list(@RequestParam(required = false) String title) {
        return documentService
                .search(title)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    // Note: UPLOAD

    @PostMapping("/upload")
    public ResponseEntity<?> upload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title) {
        try {
            // Generate a unique object name for the file
            String objectName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

            // Upload the file to MinIO
            minioStorageService.upload(objectName, file);

            // Create a new Document entity in the database
            DocumentCreateRequest req = new DocumentCreateRequest();
            req.setTitle(title);
            req.setFilename(file.getOriginalFilename());
            req.setContentType(file.getContentType());
            req.setObjectName(objectName); // Assuming you have this field in your entity

            Document savedDocument = documentService.create(req);

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
