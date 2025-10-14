package at.bif.swen.rest.mapper;

import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.entity.Document;

public final class DocumentMapper {
    private DocumentMapper() {}

    public static DocumentDto toDto(Document d) {
        return new DocumentDto(
                d.getId(),
                d.getTitle(),
                d.getFilename(),
                d.getContentType(),
                d.getSize(),
                d.getUploadedAt(),
                d.getSummary()
        );
    }
}
