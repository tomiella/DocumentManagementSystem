package at.bif.swen.rest.search;

import at.bif.swen.rest.entity.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface SearchService {

    void indexUploadedFile(Document document, MultipartFile file);

    List<UUID> searchIds(String query);

    void deleteById(UUID id);
    void updateTitleSummary(Document document);
}