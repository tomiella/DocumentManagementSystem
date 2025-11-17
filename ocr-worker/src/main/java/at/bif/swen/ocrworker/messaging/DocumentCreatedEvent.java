package at.bif.swen.ocrworker.messaging;

import java.util.UUID;

public record DocumentCreatedEvent(UUID id, String filename, String contentType, long size) {}
