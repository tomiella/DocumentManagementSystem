package at.bif.swen.rest.controller;

import at.bif.swen.rest.dto.DocumentCreateRequest;
import at.bif.swen.rest.entity.Document;
import at.bif.swen.rest.service.DocumentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import org.springframework.security.test.context.support.WithMockUser;
import at.bif.swen.rest.mapper.DocumentMapper;
import at.bif.swen.rest.service.AccessTrackingService;

@WebMvcTest(controllers = DocumentController.class)
@WithMockUser(username = "admin", roles = { "USER" })
class DocumentControllerTest {

    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @MockBean
    DocumentService documentService;
    @MockBean
    DocumentMapper documentMapper;
    @MockBean
    AccessTrackingService accessTrackingService;

    @Test
    void create_returns_201_with_location() throws Exception {
        var req = new DocumentCreateRequest("t", "f.pdf", "application/pdf", 1L, "sum");
        var saved = Document.builder()
                .id(UUID.randomUUID())
                .title("t")
                .filename("f.pdf")
                .contentType("application/pdf")
                .size(1L)
                .build();

        Mockito.when(documentService.create(req)).thenReturn(saved);

        mvc.perform(post("/documents")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"));
    }
}
