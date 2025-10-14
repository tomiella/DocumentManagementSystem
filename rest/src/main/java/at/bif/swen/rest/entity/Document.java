package at.bif.swen.rest.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "documents", indexes = {
        @Index(name = "ix_documents_title", columnList = "title")
})
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 300)
    private String title;

    @Column(nullable = false, length = 255)
    private String filename;

    @Column(nullable = false, length = 150)
    private String contentType;

    @Column(nullable = false)
    private long size;

    @Column(nullable = false)
    private OffsetDateTime uploadedAt;

    @Column(columnDefinition = "text")
    private String summary;

    @PrePersist
    public void prePersist() {
        if (id == null) id = UUID.randomUUID();
        if (uploadedAt == null) uploadedAt = OffsetDateTime.now();
    }
}
