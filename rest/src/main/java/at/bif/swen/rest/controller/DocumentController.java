package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.dto.DocumentUpdateRequest;

import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.DocumentService;
import at.bif.swen.rest.config.StorageProperties;

import jakarta.validation.Valid;

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

import java.nio.file.Path;
import java.nio.file.Files;


@RestController
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentController {

    private final DocumentService documentService;
    private final StorageProperties storageProperties;
    private final DocumentMapper mapper;


//    @PostMapping
//    public ResponseEntity<DocumentDto> create(@Valid @RequestBody DocumentCreateRequest req) {
//        Document saved = documentService.create(req);
//        return ResponseEntity.created(URI.create("/documents/" + saved.getId()))
//                .body(DocumentMapper.toDto(saved));
//    }
//
//
//    @GetMapping("/{id}")
//    public DocumentDto get(@PathVariable UUID id) {
//        return DocumentMapper.toDto(documentService.get(id));
//    }
//
//    @GetMapping
//    public List<DocumentDto> list(@RequestParam(required = false) String title) {
//        return documentService.search(title).stream().map(DocumentMapper::toDto).toList();
//    }

    @PostMapping(path="/upload", consumes = {"multipart/form-data"})
    public ResponseEntity<DocumentDto> upload(
            @RequestPart("file") MultipartFile file,
            @RequestPart("title") String title,
            @RequestPart(value ="summary", required = false) String summary

    ) throws Exception {
        var dto = documentService.createFromUpload(title,summary,file);
        return ResponseEntity.created(URI.create("/documents/" + dto.id())).body(dto);
    }

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> download(@PathVariable UUID id) throws Exception {

        var doc = documentService.get(id);

        Resource resource = documentService.loadFile(id);


        String contentType = doc.getContentType()
                != null ? doc.getContentType()
                : "application/octet-stream";

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\""+ doc.getFilename() + "\"")
                .body(resource);
    }

    @PatchMapping("/{id}")
    public DocumentDto update(@PathVariable UUID id, @Valid @RequestBody DocumentUpdateRequest req) {
        return mapper.toDto(documentService.update(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        documentService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
