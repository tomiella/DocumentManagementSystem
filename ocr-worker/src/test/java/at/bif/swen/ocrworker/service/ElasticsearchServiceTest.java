package at.bif.swen.ocrworker.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class ElasticsearchServiceTest {

    @Mock
    private ElasticsearchClient elasticsearchClient;

    @InjectMocks
    private ElasticsearchService elasticsearchService;

    @Test
    void indexDocument_shouldCallElasticsearchClient() throws IOException {
        String id = "1";
        String content = "Test Content";

        elasticsearchService.indexDocument(id, content);

        verify(elasticsearchClient, times(1)).index(any(IndexRequest.class));
    }
}
