package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final DocumentService documentService;
    private final DocumentMapper mapper;

    @GetMapping
    public List<DocumentDto> searchDocuments(@RequestParam String q) {
        return documentService.search(q)
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
