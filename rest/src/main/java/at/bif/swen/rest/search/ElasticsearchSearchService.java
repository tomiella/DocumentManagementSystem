package at.bif.swen.rest.search;

import at.bif.swen.rest.entity.Document;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import jakarta.annotation.PostConstruct;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ElasticsearchSearchService implements SearchService {

    private static final Logger log = LoggerFactory.getLogger(ElasticsearchSearchService.class);

    private final ElasticsearchClient client;

    @Value("${elasticsearch.index:documents}")
    private String index;

    public ElasticsearchSearchService(ElasticsearchClient client) {
        this.client = client;
    }

    public record SearchDocument(String id, String title, String summary, String content) {}

    @PostConstruct
    void ensureIndexExists() {
        try {
            boolean exists = client.indices().exists(i -> i.index(index)).value();
            if (!exists) {
                client.indices().create(c -> c.index(index));
                log.info("Created Elasticsearch index '{}'", index);
            }
        } catch (Exception e) {
            log.error("Elasticsearch init failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public void indexUploadedFile(Document document, MultipartFile file) {
        if (document == null || document.getId() == null) return;

        String extracted = extractPdfText(file);
        String contentForSearch = (extracted != null && !extracted.isBlank())
                ? extracted
                : (document.getSummary() != null ? document.getSummary() : "");

        indexDoc(document, contentForSearch);
    }

    @Override
    public List<UUID> searchIds(String query) {
        if (query == null || query.isBlank()) return List.of();

        try {
            SearchResponse<SearchDocument> resp = client.search(s -> s
                            .index(index)
                            .query(q -> q.multiMatch(mm -> mm
                                    .query(query)
                                    .fields("title", "summary", "content")
                                    .fuzziness("AUTO")
                            )),
                    SearchDocument.class);

            List<UUID> ids = new ArrayList<>();
            for (Hit<SearchDocument> hit : resp.hits().hits()) {
                try {
                    ids.add(UUID.fromString(hit.id()));
                } catch (Exception ignored) {
                    // ignore non-UUID ids
                }
            }
            return ids;

        } catch (Exception e) {
            log.error("Elasticsearch search failed: {}", e.getMessage(), e);
            return List.of();
        }
    }

    @Override
    public void updateTitleSummary(Document document) {
        if (document == null || document.getId() == null) return;

        String existingContent = "";
        try {
            GetResponse<SearchDocument> get = client.get(g -> g
                    .index(index)
                    .id(document.getId().toString()), SearchDocument.class);

            if (get.found() && get.source() != null && get.source().content() != null) {
                existingContent = get.source().content();
            }
        } catch (Exception ignored) {
            // ok
        }

        indexDoc(document, existingContent);
    }

    @Override
    public void deleteById(UUID id) {
        if (id == null) return;

        try {
            client.delete(d -> d.index(index).id(id.toString()));
        } catch (Exception e) {
            log.warn("Elasticsearch delete failed for {}: {}", id, e.getMessage());
        }
    }

    private void indexDoc(Document doc, String content) {
        if (doc == null || doc.getId() == null) return;

        try {
            SearchDocument sd = new SearchDocument(
                    doc.getId().toString(),
                    doc.getTitle(),
                    doc.getSummary(),
                    content == null ? "" : content
            );

            client.index(i -> i.index(index).id(sd.id()).document(sd));
            log.info("Indexed document {} into '{}'", doc.getId(), index);

        } catch (Exception e) {
            log.error("Elasticsearch index failed for {}: {}", doc.getId(), e.getMessage(), e);
        }
    }

    private String extractPdfText(MultipartFile file) {
        if (file == null || file.isEmpty()) return "";

        String contentType = file.getContentType();
        if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) return "";

        try (PDDocument pdf = PDDocument.load(file.getInputStream())) {
            return new PDFTextStripper().getText(pdf);
        } catch (Exception e) {
            log.warn("Failed to extract text from PDF for {}: {}", file.getOriginalFilename(), e.getMessage());
            return "";
        }
    }
}