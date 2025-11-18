package at.bif.swen.rest.mapper;

import at.bif.swen.rest.dto.DocumentDto;
import at.bif.swen.rest.entity.Document;
import org.springframework.stereotype.Component;


@Component
public class DocumentMapper {

    public DocumentDto toDto(Document d) {
        if (d == null) return null;
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

