package at.bif.swen.ocrworker.messaging;

import java.util.UUID;

public record OcrResultEvent(
                UUID documentId,
                String ocrText,
                String summary) {
}
