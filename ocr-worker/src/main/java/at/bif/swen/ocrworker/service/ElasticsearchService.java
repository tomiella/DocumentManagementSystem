package at.bif.swen.ocrworker.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ElasticsearchService {

    private final ElasticsearchClient elasticsearchClient;

    public void indexDocument(String id, String content) {
        try {
            Map<String, Object> document = new HashMap<>();
            document.put("id", id);
            document.put("content", content);

            IndexRequest<Map<String, Object>> request = IndexRequest.of(i -> i
                    .index("documents")
                    .id(id)
                    .document(document));

            elasticsearchClient.index(request);
            log.info("Indexed document id={} to Elasticsearch", id);

        } catch (IOException e) {
            log.error("Failed to index document id={}", id, e);
            // We might want to throw a runtime exception or handle it differently depending
            // on requirements
            // For now, logging error is sufficient as we don't want to break the whole flow
            // if ES is down?
            // Actually, usually we want to know. But let's log and proceed or throw?
            // Requirement says "Store the text-content". Let's assume best effort or retry.
            // For simplicity: log error.
        }
    }
}
