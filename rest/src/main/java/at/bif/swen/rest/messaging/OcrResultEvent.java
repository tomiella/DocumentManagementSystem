package at.bif.swen.rest.messaging;

import java.util.UUID;

public record OcrResultEvent(
        UUID documentId,
        String ocrText) {
}
