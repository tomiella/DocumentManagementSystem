package at.bif.swen.rest.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record DocumentDto(
                UUID id,
                String title,
                String filename,
                String contentType,
                long size,
                OffsetDateTime uploadedAt,
                String summary,
                String ocrText) {
}
